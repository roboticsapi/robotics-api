/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.activity.AbstractPlannedRtActivity;
import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActivityProperty;
import org.roboticsapi.activity.ActivityStatus;
import org.roboticsapi.cartesianmotion.action.CartesianErrorCorrection;
import org.roboticsapi.cartesianmotion.action.CartesianMotionPlan;
import org.roboticsapi.cartesianmotion.action.HoldCartesianPosition;
import org.roboticsapi.cartesianmotion.action.LIN;
import org.roboticsapi.cartesianmotion.action.LINFromMotion;
import org.roboticsapi.cartesianmotion.action.PathMotion;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.cartesianmotion.parameter.CartesianBlendingParameter;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.state.OrState;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.TransformationSensor;

public class LinearMotionActivityImpl extends AbstractPlannedRtActivity<ActuatorDriver, CartesianMotionDevice>
		implements PlannedCartesianActivity {
	private final CartesianMotionDevice robot;
	private final Frame to;
	private final MotionCenterParameter motionCenter;
	private final DeviceParameterBag parameters;
	private final double speedFactor;
	Map<MotionTimeProgressState, List<State>> motionTimeProgressStates = new IdentityHashMap<MotionTimeProgressState, List<State>>();
	private final List<FrameProgressState<CartesianMotionPlan>> frameProgressStates = new ArrayList<LinearMotionActivityImpl.FrameProgressState<CartesianMotionPlan>>();

	public LinearMotionActivityImpl(String name, CartesianMotionDevice robot, Frame to, double speedFactor,
			MotionCenterParameter motionCenter, DeviceParameterBag parameters) {
		super(name, robot);
		this.robot = robot;
		this.speedFactor = speedFactor;
		this.motionCenter = motionCenter;
		this.parameters = parameters;
		this.to = to;
	}

	protected Command createCommandForBlendingCase(Activity prevActivity, State takeoverAllowedState,
			List<ActivityProperty> resultingProperties) throws RoboticsException {

		if (prevActivity.getProperty(getDevice(), FrameGoalProperty.class) == null) {
			return null;
		}

		// use previous goal as start
		FrameGoalProperty goal = prevActivity.getProperty(getDevice(), FrameGoalProperty.class);

		Transformation fromTransformation = calculateFromTransformationForMotionCenter(goal.getMotionCenter(),
				goal.getGoal());

		Frame from = new Frame("[MotionInterfaceImpl.lin start frame]", to, fromTransformation);

		BlendingStartCartesianStatusProperty blendStart = prevActivity.getProperty(getDevice(),
				BlendingStartCartesianStatusProperty.class);
		if (blendStart == null) {
			return null;
		}
		Transformation motionCenterDiff = blendStart.getMotionCenter()
				.getTransformationTo(motionCenter.getMotionCenter());
		Frame realFrom = new Frame("[MotionInterfaceImpl.lin blending start frame]", to,
				to.getTransformationTo(blendStart.getFrame()).multiply(motionCenterDiff));

		Twist fromVel = blendStart.getTwist().changePivot(motionCenterDiff.getTranslation())
				.changeOrientation(motionCenterDiff.getRotation());

		LINFromMotion blendedLIN = new LINFromMotion(speedFactor, from, to, realFrom, fromVel);

		RuntimeCommand blendedCmd;

		CartesianErrorCorrection action = new CartesianErrorCorrection(blendedLIN, motionCenter.getMotionCenter(),
				blendedLIN.getRealFrom(), new Transformation());
		blendedCmd = robot.getDriver().getRuntime().createRuntimeCommand(robot, action, parameters);

		// for all MotionTimePercentStates that were requested by
		// anyone, we raise the according Action state
		bindPlanToProgressStates(blendedCmd.getPlan(action));
		bindProgressStates(blendedLIN);

		blendedCmd.addTakeoverAllowedCondition(takeoverAllowedState);

		return blendedCmd;

	}

	private Transformation calculateFromTransformationForMotionCenter(Frame motionCenterFrame, Frame from)
			throws RoboticsException {
		try {
			return to.getTransformationTo(from, false)
					.multiply(motionCenterFrame.getTransformationTo(motionCenter.getMotionCenter()));
		} catch (TransformationException e) {
			throw new RoboticsException(
					"Motion goal Frame not statically linked to start Frame - did you try to drive to a moving Frame?",
					e);
		}
	}

	private Command createRegularLin(Frame motionCenterFrame, Frame start, State takeoverAllowedState,
			List<ActivityProperty> resultingProperties) throws RoboticsException {
		Transformation fromTransformation = calculateFromTransformationForMotionCenter(motionCenterFrame, start);

		Frame from = new Frame("[MotionInterfaceImpl.lin start frame]", to, fromTransformation);
		LIN regularLIN = new LIN(speedFactor, from, to);

		// create Command for the regular LIN
		RuntimeCommand regularCmd = robot.getDriver().getRuntime().createRuntimeCommand(robot, regularLIN, parameters);

		// for all MotionTimePercentStates that were requested by anyone,
		// we raise the according Action state
		bindPlanToProgressStates(regularCmd.getPlan(regularLIN));
		bindProgressStates(regularLIN);

		State blendState = takeoverAllowedState;
		// Takeover
		if (takeoverAllowedState != null) {
			regularCmd.addTakeoverAllowedCondition(takeoverAllowedState);
			List<State> takeoverIsMotionTime = motionTimeProgressStates.get(takeoverAllowedState);
			if (takeoverIsMotionTime != null) {
				blendState = takeoverIsMotionTime.get(takeoverIsMotionTime.size() - 1);
			}
		}

		if (blendState != null) {
			CartesianMotionPlan plan = regularCmd.getPlan(regularLIN);
			Double blendTime = plan.getTimeAtFirstOccurence(blendState);

			resultingProperties.add(new BlendingStartCartesianStatusProperty(motionCenter.getMotionCenter(),
					plan.getBaseFrame().plus(plan.getTransformationAt(blendTime)),
					plan.getTwistAt(blendTime).changeOrientation(plan.getTransformationAt(blendTime).getRotation())));
		}

		resultingProperties.add(new FrameGoalProperty(to, motionCenter.getMotionCenter()));

		return regularCmd;
	}

	protected Command createCommandForResultProperty(Activity prevActivity, State takeoverAllowedState,
			List<ActivityProperty> resultingProperties) throws RoboticsException {
		if (prevActivity.getProperty(getDevice(), FrameGoalProperty.class) == null) {
			return null;
		}

		// use previous goal as start
		FrameGoalProperty goal = prevActivity.getProperty(getDevice(), FrameGoalProperty.class);

		return createRegularLin(goal.getMotionCenter(), goal.getGoal(), takeoverAllowedState, resultingProperties);
	}

	protected Command createCommandForCompletedPredecessor(Activity prevActivity, State takeoverAllowedState,
			List<ActivityProperty> resultingProperties) throws RoboticsException {

		// use current position as start
		Frame from = new Frame("[MotionInterfaceImpl.lin start frame]", to,
				to.getTransformationTo(motionCenter.getMotionCenter(), true));

		return createRegularLin(motionCenter.getMotionCenter(), from, takeoverAllowedState, resultingProperties);
	}

	private Command createMaintainingCommand() throws RoboticsException {

		// if target is moving relative to us, we need a
		// HoldCartesianPosition
		try {
			robot.getReferenceFrame().getTransformationTo(to, false);
			return null;
		} catch (TransformationException e) {
			return robot.getDriver().getRuntime().createRuntimeCommand(robot, new HoldCartesianPosition(to),
					parameters);
		}
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		List<ActivityProperty> properties = new ArrayList<ActivityProperty>();

		State takeoverAllowedState = calculateTakeoverAllowedState();

		Command maintaining = createMaintainingCommand();
		Command result = null;
		boolean canTakeover = false;

		if (prevActivity == null || (prevActivity.getStatus() == ActivityStatus.COMPLETED)) {
			Command regular = createCommandForCompletedPredecessor(prevActivity, takeoverAllowedState, properties);
			result = regular;
			canTakeover = false;

		} else if (prevActivity.getStatus() == ActivityStatus.MAINTAINING) {
			Command regular = createCommandForResultProperty(prevActivity, takeoverAllowedState, properties);
			if (regular == null) {
				throw new ActivityNotCompletedException(prevActivity);
			}
			result = regular;
			canTakeover = true;
		} else {
			Command blending = createCommandForBlendingCase(prevActivity, takeoverAllowedState, properties);
			Command regular = createCommandForResultProperty(prevActivity, takeoverAllowedState, properties);

			if (regular == null) {
				throw new ActivityNotCompletedException(prevActivity);
			}

			if (blending == null) {
				result = regular;
				canTakeover = false;

			} else {
				BooleanSensor condition = createConditionForResult(prevActivity);

				TransactionCommand transaction = regular.getRuntime().createTransactionCommand(regular, blending);

				transaction.addStartCommand(condition, regular);
				transaction.addStartCommand(condition.not(), blending);
				transaction.addStateFirstEnteredHandler(transaction.getCancelState(), new CommandCanceller(regular));
				transaction.addStateFirstEnteredHandler(transaction.getCancelState(), new CommandCanceller(blending));

				transaction.addDoneStateCondition(regular.getDoneState());
				transaction.addDoneStateCondition(blending.getDoneState());
				result = transaction;
				canTakeover = true;
			}
		}

		result = includeBlendingAndMaintaining(result, takeoverAllowedState, maintaining);
		if (maintaining != null) {
			setCommand(result, prevActivity, maintaining.getStartedState());
		} else {
			setCommand(result, prevActivity);
		}

		for (ActivityProperty prop : properties) {
			addProperty(robot, prop);
		}

		return canTakeover;

	}

	private State calculateTakeoverAllowedState() {
		CartesianBlendingParameter blendingParameter = parameters.get(CartesianBlendingParameter.class);

		if (blendingParameter != null && blendingParameter.getAtProgress() != null) {
			// event triggering start of blending
			return getMotionTimeProgress(blendingParameter.getAtProgress());
		} else if (blendingParameter != null && blendingParameter.getBlendFrame() != null) {
			return getMotionTimeProgress(blendingParameter.getBlendFrame());
		} else if (blendingParameter != null && blendingParameter.getBlendingPathDistance() != null) {

			throw new UnsupportedOperationException("Distance based blending not yet implemented");
		} else {
			return null;
		}
	}

	private BooleanSensor createConditionForResult(Activity prevActivity) {
		FrameGoalProperty goal = prevActivity.getProperty(getDevice(), FrameGoalProperty.class);

		BooleanSensor regularCondition = createPositionComparer(
				robot.getReferenceFrame().getRelationSensor(goal.getGoal()).getTransformationSensor(),
				robot.getReferenceFrame().getRelationSensor(motionCenter.getMotionCenter()).getTransformationSensor());
		return regularCondition;
	}

	private Command includeBlendingAndMaintaining(Command command, State blendingCondition, Command maintaining)
			throws RoboticsException {

		if (blendingCondition != null) {
			command.addTakeoverAllowedCondition(blendingCondition.and(command.getCancelState().not()));
		}

		finishProgressStates();

		// for (Entry<MotionTimeProgressState, List<State>> s :
		// motionTimeProgressStates
		// .entrySet()) {
		// s.getKey().setOther(
		// new OrState(s.getValue().toArray(
		// new State[s.getValue().size()])));
		// }

		if (maintaining != null) {
			TransactionCommand ret = command.getRuntime().createTransactionCommand(command, maintaining);
			ret.addStartCommand(command);
			ret.addStateEnteredHandler(command.getDoneState().and(ret.getCancelState().not()),
					new CommandStarter(maintaining));
			ret.addDoneStateCondition(maintaining.getDoneState());
			ret.addDoneStateCondition(command.getCancelState().and(command.getDoneState()));

			ret.addTakeoverAllowedCondition(maintaining.getStartedState());
			ret.addTakeoverAllowedCondition(command.getTakeoverAllowedState());

			ret.addStateEnteredHandler(ret.getCancelState().and(command.getActiveState()),
					new CommandCanceller(command));

			return ret;
		} else {
			return command;
		}
	}

	private BooleanSensor createPositionComparer(TransformationSensor expected, TransformationSensor actual) {
		BooleanSensor sensor = expected.getRotationSensor().equals(actual.getRotationSensor(), 0.01);

		sensor = sensor.and(
				expected.getTranslationSensor().getXSensor().equals(actual.getTranslationSensor().getXSensor(), 0.001));

		sensor = sensor.and(
				expected.getTranslationSensor().getYSensor().equals(actual.getTranslationSensor().getYSensor(), 0.001));

		sensor = sensor.and(
				expected.getTranslationSensor().getZSensor().equals(actual.getTranslationSensor().getZSensor(), 0.001));

		return sensor;
	}

	private void bindPlanToProgressStates(CartesianMotionPlan plan) {
		for (FrameProgressState<CartesianMotionPlan> s : frameProgressStates) {
			s.addPlan(plan);
		}

	}

	@Override
	public State getMotionTimeProgress(Frame f) {
		FrameProgressState<CartesianMotionPlan> frameProgressState = new FrameProgressState<CartesianMotionPlan>(f);
		frameProgressStates.add(frameProgressState);
		return frameProgressState;
	}

	protected class FrameProgressState<T extends CartesianMotionPlan>
			extends PlannedRtActivityProgressState<T, PathMotion<T>> {

		private final Frame f;
		private final List<T> plans = new ArrayList<T>();

		public FrameProgressState(Frame f) {
			this.f = f;
		}

		public void addPlan(T plan) {
			this.plans.add(plan);
		}

		@Override
		protected State determineActionState(PathMotion<T> action) {
			if (plans.size() == 0) {
				return State.False();
			} else if (plans.size() == 1) {
				return action.getMotionTimeProgress(f, plans.get(0));
			} else {
				OrState or = new OrState();
				for (T p : plans) {
					ActionState motionTimeProgress = action.getMotionTimeProgress(f, p);
					if (motionTimeProgress != null) {
						or.addState(motionTimeProgress);
					}
				}
				if (or.getStates().size() == 0) {
					return null;
				}
				return or;
			}
		}
	}

}