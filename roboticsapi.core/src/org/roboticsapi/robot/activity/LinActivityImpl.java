/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import org.roboticsapi.activity.AbstractPlannedRtActivity;
import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActivityStatus;
import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.cartesianmotion.action.CartesianErrorCorrection;
import org.roboticsapi.cartesianmotion.action.CartesianMotionPlan;
import org.roboticsapi.cartesianmotion.action.HoldCartesianPosition;
import org.roboticsapi.cartesianmotion.action.LIN;
import org.roboticsapi.cartesianmotion.action.LINFromMotion;
import org.roboticsapi.cartesianmotion.activity.BlendingStartCartesianStatusProperty;
import org.roboticsapi.cartesianmotion.activity.FrameGoalProperty;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.multijoint.action.JointMotionPlan;
import org.roboticsapi.multijoint.activity.BlendingStartJointStatusProperty;
import org.roboticsapi.multijoint.activity.JointGoalProperty;
import org.roboticsapi.multijoint.parameter.BlendingParameter;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.robot.RobotArmDriver;
import org.roboticsapi.robot.action.NullspacePTP;
import org.roboticsapi.robot.action.NullspacePTPFromMotion;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.TransformationSensor;

final class LinActivityImpl extends AbstractPlannedRtActivity<RobotArmDriver, RobotArm> implements PlannedRtActivity {
	private final RobotArm robot;
	private LIN regularLIN;
	private LINFromMotion blendedLIN;
	private final Frame to;
	private final MotionCenterParameter motionCenter;
	private final DeviceParameterBag parameters;
	private double[] nullspaceJoints;
	private NullspacePTP regularNullspace;
	private NullspacePTPFromMotion blendedNullspace;
	private final double speedFactor;

	LinActivityImpl(String name, RobotArm robot, Frame to, double[] nullspaceJoints, double speedFactor,
			MotionCenterParameter motionCenter, DeviceParameterBag parameters) {
		super(name, robot);
		this.robot = robot;
		this.nullspaceJoints = nullspaceJoints;
		this.speedFactor = speedFactor;
		this.motionCenter = motionCenter;
		this.parameters = parameters;
		this.to = to;
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		boolean canTakeover = false;

		BlendingParameter blendingParameter = parameters.get(BlendingParameter.class);

		State blendStartEvent = null;
		if (blendingParameter != null) {
			// event triggering start of blending
			blendStartEvent = getMotionTimeProgress(blendingParameter.getAtProgress());
		}
		regularNullspace = null;
		blendedNullspace = null;

		if (prevActivity != null && prevActivity.getProperty(getDevice(), FrameGoalProperty.class) != null) {

			// use previous goal as start
			FrameGoalProperty goal = prevActivity.getProperty(getDevice(), FrameGoalProperty.class);

			Transformation fromTransformation = null;

			// TODO: Move this check to some superclass?
			try {
				fromTransformation = to.getTransformationTo(goal.getGoal(), false)
						.multiply(goal.getMotionCenter().getTransformationTo(motionCenter.getMotionCenter()));
			} catch (TransformationException e) {
				throw new RoboticsException(
						"Motion goal Frame not statically linked to start Frame - did you try to drive to a moving Frame?",
						e);
			}

			Frame from = new Frame("[MotionInterfaceImpl.lin start frame]", to, fromTransformation);
			regularLIN = new LIN(speedFactor, from, to);
			JointGoalProperty jointgoal = prevActivity.getProperty(getDevice(), JointGoalProperty.class);
			if (jointgoal != null) {
				// we know the previous joint goal? Create nullspace PTP.
				if (nullspaceJoints == null) {
					nullspaceJoints = jointgoal.getGoal();
				}
				regularNullspace = new NullspacePTP(robot, jointgoal.getGoal(), nullspaceJoints, regularLIN);
			}

			// additionally, if the predecessing Activity provides
			// information about its state at the given blending condition,
			// we can actually use a planned blending LIN motion
			BlendingStartCartesianStatusProperty blendStart = prevActivity.getProperty(getDevice(),
					BlendingStartCartesianStatusProperty.class);
			if (blendStart != null) {
				Transformation motionCenterDiff = blendStart.getMotionCenter()
						.getTransformationTo(motionCenter.getMotionCenter());
				Frame realFrom = new Frame("[MotionInterfaceImpl.lin blending start frame]", to,
						to.getTransformationTo(blendStart.getFrame()).multiply(motionCenterDiff));

				Twist fromVel = blendStart.getTwist().changePivot(motionCenterDiff.getTranslation())
						.changeOrientation(motionCenterDiff.getRotation());

				blendedLIN = new LINFromMotion(speedFactor, from, to, realFrom, fromVel);

				BlendingStartJointStatusProperty jointBlendStart = prevActivity.getProperty(getDevice(),
						BlendingStartJointStatusProperty.class);
				if (jointBlendStart != null && jointgoal != null) {
					blendedNullspace = new NullspacePTPFromMotion(robot, jointgoal.getGoal(), nullspaceJoints,
							jointBlendStart.getPos(), jointBlendStart.getVel(), new CartesianErrorCorrection(blendedLIN,
									motionCenter.getMotionCenter(), blendedLIN.getRealFrom(), new Transformation()));
				}

				canTakeover = true;
			} else {
				// otherwise we cannot blend.
				canTakeover = false;
			}

		} else if (prevActivity != null && prevActivity.getStatus() == ActivityStatus.RUNNING) {
			// notify that prevActivity has to be completed for
			// execution
			throw new ActivityNotCompletedException(prevActivity);

		} else {
			// use current position as start
			Frame from = new Frame("[MotionInterfaceImpl.lin start frame]", to,
					to.getTransformationTo(motionCenter.getMotionCenter(), true));
			regularLIN = new LIN(speedFactor, from, to);

			// Create nullspace PTP.
			double[] startjoints = robot.getJointAngles();
			if (nullspaceJoints == null) {
				nullspaceJoints = startjoints;
			}
			regularNullspace = new NullspacePTP(robot, startjoints, nullspaceJoints, regularLIN);

		}

		// FIXME: For now, we ignore the nullspace motion because frame
		// projectors cannot handle them (bug 61).
		// This should be done differently.
		// regularNullspace = null;

		// create Command for the regular LIN
		RuntimeCommand regularCmd = robot.getDriver().getRuntime().createRuntimeCommand(robot,
				regularNullspace != null ? regularNullspace : regularLIN, parameters);

		TransactionCommand trans = null;
		State maintainingState = null;
		RuntimeCommand hold = null;
		// if target is moving relative to us, we need a
		// HoldCartesianPosition
		try {
			robot.getBase().getTransformationTo(to, false);
		} catch (TransformationException e) {
			// we use a TransactionCommand for implementing a choice between
			// the two LIN Commands, depending on the actual state of things
			trans = robot.getDriver().getRuntime().createTransactionCommand();
			trans.addStartCommand(regularCmd);

			hold = robot.getDriver().getRuntime().createRuntimeCommand(robot, new HoldCartesianPosition(to),
					parameters);

			trans.addCommand(hold);

			trans.addStateEnteredHandler(regularCmd.getDoneState(), new CommandStarter(hold));

			trans.addStateEnteredHandler(trans.getCancelState().and(hold.getActiveState()), new CommandCanceller(hold));

			trans.addTakeoverAllowedCondition(hold.getStartedState().and(trans.getCancelState().not()));

			maintainingState = hold.getStartedState();
		}

		// if we have prepared for blending over the predecessing activity,
		// we also process the LIN for planned blending
		if (blendedLIN != null) {

			if (trans == null) {
				trans = robot.getDriver().getRuntime().createTransactionCommand(regularCmd);
			}

			// if we reached the start point of our regular LIN (i.e. the
			// previous motion was executed completely) this LIN is the one to
			// start
			BooleanSensor regularCondition = createPositionComparer(
					robot.getBase().getRelationSensor(regularLIN.getFrom()).getTransformationSensor(),
					robot.getBase().getRelationSensor(motionCenter.getMotionCenter()).getTransformationSensor());
			trans.addStartCommand(regularCondition, regularCmd);

			// process the blended LIN

			Command blendedCmd;

			if (blendedNullspace != null) {
				blendedCmd = robot.getDriver().getRuntime().createRuntimeCommand(robot, blendedNullspace, parameters);
			} else {
				blendedCmd = robot.getDriver().getRuntime()
						.createRuntimeCommand(robot, new CartesianErrorCorrection(blendedLIN,
								motionCenter.getMotionCenter(), blendedLIN.getRealFrom(), new Transformation()),
								parameters);
			}

			// for all MotionTimePercentStates that were requested by
			// anyone, we raise the according Action state
			bindProgressStates(blendedLIN);
			bindProgressStates(regularLIN);
			// for (MotionTimeProgressState s :
			// getRelevantMotionTimeProgressStates()) {
			// s.setOther(blendedLIN.getMotionTimeProgress((float) s.progress)
			// .or(regularLIN
			// .getMotionTimeProgress((float) s.progress)));
			// }

			// if we reached the start point of our planned PTP (i.e. the
			// previous motion was executed completely) this PTP is the one
			// to start
			trans.addStartCommand(regularCondition.not(), blendedCmd);

			trans.addStateEnteredHandler(trans.getCancelState().and(regularCmd.getActiveState()),
					new CommandCanceller(regularCmd));

			trans.addStateEnteredHandler(trans.getCancelState().and(blendedCmd.getActiveState()),
					new CommandCanceller(blendedCmd));

			// if we may be blended, allow this at Command level
			if (blendStartEvent != null) {
				trans.addTakeoverAllowedCondition(blendStartEvent.and(trans.getCancelState().not()));
			}

			if (hold != null) {
				trans.addStateEnteredHandler(blendedCmd.getDoneState(), new CommandStarter(hold));
			}

			if (maintainingState != null) {
				setCommand(trans, prevActivity, maintainingState);
			} else {
				setCommand(trans, prevActivity);
			}
		} else {
			// for all MotionTimePercentStates that were requested by anyone,
			// we raise the according Action state
			bindProgressStates(regularLIN);
			// for (MotionTimeProgressState s :
			// getRelevantMotionTimeProgressStates()) {
			// s.setOther(regularLIN.getMotionTimeProgress((float) s.progress));
			// }

			if (trans != null) {
				// if we may be blended, allow this at Command level
				if (blendStartEvent != null) {
					trans.addTakeoverAllowedCondition(blendStartEvent.and(trans.getCancelState().not()));
				}

				trans.addStateEnteredHandler(trans.getCancelState().and(regularCmd.getActiveState()),
						new CommandCanceller(regularCmd));

				if (maintainingState != null) {
					setCommand(trans, prevActivity, maintainingState);
				} else {
					setCommand(trans, prevActivity);
				}
			}

			else {
				// if we may be blended, allow this at Command level
				if (blendStartEvent != null) {
					regularCmd.addTakeoverAllowedCondition(blendStartEvent.and(regularCmd.getCancelState().not()));
				}

				if (maintainingState != null) {
					setCommand(regularCmd, prevActivity, maintainingState);
				} else {
					setCommand(regularCmd, prevActivity);
				}
			}
		}

		finishProgressStates();

		addProperty(robot, new FrameGoalProperty(to, motionCenter.getMotionCenter()));

		addProperty(robot, getBlendingStartCartesianStatusProperty(regularCmd.getPlan(regularLIN), blendingParameter));

		if (nullspaceJoints != null) {
			double[] jointgoal = robot.getInverseKinematics(to, motionCenter.getMotionCenter(), nullspaceJoints);
			addProperty(robot, new JointGoalProperty(jointgoal));

			addProperty(robot,
					getBlendingStartJointStatusProperty(
							regularNullspace != null ? regularCmd.getPlan(regularNullspace) : null,
							regularCmd.getPlan(regularLIN), blendingParameter));
		}
		return canTakeover;

	}

	private BlendingStartJointStatusProperty getBlendingStartJointStatusProperty(JointMotionPlan plan,
			CartesianMotionPlan cPlan, BlendingParameter blendingParameter) {
		if (blendingParameter == null) {
			return null;
		}

		if (plan != null) {

			Double blendTime = plan.getTimeAtFirstOccurence(
					regularLIN.getMotionTimeProgress((float) blendingParameter.getAtProgress()));

			if (blendTime == null) {
				return null;
			}

			return new BlendingStartJointStatusProperty(plan.getJointPositionsAt(blendTime),
					plan.getJointVelocitiesAt(blendTime));
		} else if (cPlan != null) {
			// TODO: estimate joint positions somehow

			// double[] pos = null;
			// double[] vel = null;
			// robot.getDriver().g
			// return new BlendingStartJointStatusProperty(pos, vel);

			return null;
		} else {
			return null;
		}
	}

	private BlendingStartCartesianStatusProperty getBlendingStartCartesianStatusProperty(CartesianMotionPlan plan,
			BlendingParameter blendingParameter) {
		if (blendingParameter == null) {
			return null;
		}

		Double blendTime = plan
				.getTimeAtFirstOccurence(regularLIN.getMotionTimeProgress((float) blendingParameter.getAtProgress()));

		return new BlendingStartCartesianStatusProperty(motionCenter.getMotionCenter(),
				plan.getBaseFrame().plus(plan.getTransformationAt(blendTime)),
				plan.getTwistAt(blendTime).changeOrientation(plan.getTransformationAt(blendTime).getRotation()));
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

}