/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.action.WrappedAction;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.runtime.PlannedSingleDeviceActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.DynamicScopeRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.PoseCoincidence;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Velocity;
import org.roboticsapi.core.world.World;
import org.roboticsapi.framework.cartesianmotion.action.CartesianErrorCorrection;
import org.roboticsapi.framework.cartesianmotion.action.CartesianMotionPlan;
import org.roboticsapi.framework.cartesianmotion.action.LIN;
import org.roboticsapi.framework.cartesianmotion.action.LINFromMotion;
import org.roboticsapi.framework.cartesianmotion.action.PathMotion;
import org.roboticsapi.framework.cartesianmotion.activity.PlannedCartesianActivity;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianBlendingParameter;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;

public class LinearMotionActivityImpl extends PlannedSingleDeviceActivity implements PlannedCartesianActivity {
	private final Pose to;
	private final MotionCenterParameter motionCenter;
	private final DeviceParameterBag parameters;
	private final double speedFactor;
	private final CartesianMotionDevice robot;

	public LinearMotionActivityImpl(String name, ActuatorDriver driver, Pose to, double speedFactor,
			MotionCenterParameter motionCenter, DeviceParameterBag parameters) throws RoboticsException {
		super(name, driver);
		this.robot = (CartesianMotionDevice) driver.getDevice();
		this.speedFactor = speedFactor;
		this.motionCenter = motionCenter;
		this.parameters = parameters;
		this.to = to;
	}

	private Command createBlendingLin(Pose oldGoal, Pose oldGoalMC, Pose blendPos, Pose blendPosMC, Velocity blendVel,
			Pose blendVelMC) throws RoboticsException {

		Pose mc = motionCenter.getMotionCenter();
		Pose from = convertMC(oldGoal, oldGoalMC, mc);
		Pose realPos = convertMC(blendPos, blendPosMC, mc);
		Velocity realVel = new PoseCoincidence(blendPos, blendPosMC).getVelocity(blendVel.getReferenceFrame(), mc,
				blendVel);

		LINFromMotion blendedLIN = new LINFromMotion(speedFactor, from, to, realPos, realVel);
		CartesianErrorCorrection action = new CartesianErrorCorrection(blendedLIN, realPos,
				motionCenter.getMotionCenter(), World.getCommandedTopology().forRuntime(getRuntime()));

		return createRuntimeCommand(action, parameters);

	}

	private Pose convertMC(Pose oldGoal, Pose oldGoalMC, Pose mc) throws TransformationException {
		return new PoseCoincidence(oldGoal, oldGoalMC).getPose(oldGoal.getReference(), mc);
	}

	private Command createRegularLin(Pose start, Pose startMC) throws RoboticsException {

		Pose from = convertMC(start, startMC, motionCenter.getMotionCenter());
		LIN lin = new LIN(speedFactor, from, to);
		return createRuntimeCommand(lin, parameters);

	}

	// private Command createMaintainingCommand() throws RoboticsException {
	//
	// // if target is moving relative to us, we need a
	// // HoldCartesianPosition
	// try {
	// robot.getReferenceFrame().getTransformationTo(to, false);
	// return null;
	// } catch (TransformationException e) {
	// return robot
	// .getDriver()
	// .getRuntime()
	// .createRuntimeCommand(robot, new HoldCartesianPosition(to),
	// parameters);
	// }
	// }

	private RealtimeBoolean calculateTakeoverAllowedState() {
		CartesianBlendingParameter blendingParameter = parameters.get(CartesianBlendingParameter.class);

		if (blendingParameter != null && blendingParameter.getAtProgress() != null) {
			// event triggering start of blending
			if (blendingParameter.getAtProgress() < 1) {
				return getMotionTimeProgress(blendingParameter.getAtProgress());
			}
		} else if (blendingParameter != null && blendingParameter.getBlendFrame() != null) {
			return getMotionTimeProgress(blendingParameter.getBlendFrame());
		} else if (blendingParameter != null && blendingParameter.getBlendingPathDistance() != null) {
			throw new UnsupportedOperationException("Distance based blending not yet implemented");
		}
		return null;
	}

	@Override
	public RealtimeBoolean getMotionTimeProgress(final Frame f) {
		return new DynamicScopeRealtimeBoolean() {
			@Override
			public RealtimeBoolean getScopedState(Command scope) {
				if (scope instanceof RuntimeCommand && ((RuntimeCommand) scope).getAction() instanceof PathMotion<?>) {
					RuntimeCommand command = (RuntimeCommand) scope;
					PathMotion<?> action = (PathMotion<?>) command.getAction();
					CartesianMotionPlan plan = command.getPlan(action);
					return action.getMotionTimeProgress(scope, f, plan);
				}
				return null;
			}
		};
	}

	protected PathMotion<?> findMotion(Command command) {
		if (command instanceof RuntimeCommand) {
			return findMotion(((RuntimeCommand) command).getAction());
		}
		return null;
	}

	protected PathMotion<?> findMotion(Action action) {
		if (action instanceof PathMotion<?>) {
			return (PathMotion<?>) action;
		} else if (action instanceof WrappedAction) {
			return findMotion(((WrappedAction) action).getWrappedAction());
		}
		return null;
	}

	@Override
	protected Set<ActivityResult> getResultsForCommand(Command command) throws RoboticsException {
		RuntimeCommand c = (RuntimeCommand) command;
		PathMotion<?> motion = findMotion(command);
		CartesianMotionPlan plan = c.getPlan(motion);

		Pose mc = motionCenter.getMotionCenter();
		GoalPoseProperty goal = new GoalPoseProperty(to.asRealtimeValue(), mc);
		CommandedPoseProperty pose = new CommandedPoseProperty(to, mc);
		CommandedVelocityProperty vel = new CommandedVelocityProperty(
				new Velocity(to.getReference(), null, to.getOrientation(), new Twist()), mc);

		ActivityResult completed = completionResult(c.getCompletionResult(), goal, pose, vel);
		ActivityResult cancelled = cancelResult(c.getCancelResult(), goal);

		RealtimeBoolean blendState = calculateTakeoverAllowedState();
		if (blendState != null) {
			blendState = blendState.getForScope(command);
		}

		if (blendState != null) {
			Double blendTime = plan.getTimeAtFirstOccurence(blendState);
			pose = new CommandedPoseProperty(plan.getBaseFrame(), plan.getTransformationAt(blendTime), mc);
			vel = new CommandedVelocityProperty(new Velocity(plan.getBaseFrame(), null,
					plan.getBaseFrame().asOrientation(), plan.getTwistAt(blendTime)), mc);

			ActivityResult blending = takeoverResult(
					c.addTakeoverResult("Blending", blendState.and(c.getCancelState().not()), false), goal, pose, vel);

			return activityResultSet(completed, blending, cancelled);
		} else {
			return activityResultSet(completed, cancelled);
		}
	}

	@Override
	protected Command getCommandForResult(ActivityResult result) throws RoboticsException {

		if (result == null) {
			// no predecessor
			Transformation start = robot.getReferenceFrame().asPose()
					.getCommandedTransformationTo(motionCenter.getMotionCenter());
			Pose from = new Pose(robot.getReferenceFrame(), start);

			return createRegularLin(from, motionCenter.getMotionCenter());

		} else {

			GoalPoseProperty goalPose = result.getMetadata(getDevice(), GoalPoseProperty.class);

			CommandedPoseProperty effectivePose = result.getMetadata(getDevice(), CommandedPoseProperty.class);
			CommandedVelocityProperty effectiveVelocity = result.getMetadata(getDevice(),
					CommandedVelocityProperty.class);

			if (effectiveVelocity != null && effectivePose != null) {
				if (effectiveVelocity.getVelocity().getTwist().getTransVel().getLength() <= 0.01
						&& effectiveVelocity.getVelocity().getTwist().getRotVel().getLength() <= 0.01) {
					// standing still

					return createRegularLin(effectivePose.getPose(), effectivePose.getMotionCenter());

				} else {
					// still moving

					return createBlendingLin(goalPose.getPose().getCurrentValue(), goalPose.getMotionCenter(),
							effectivePose.getPose(), effectivePose.getMotionCenter(), effectiveVelocity.getVelocity(),
							effectiveVelocity.getMotionCenter());
				}
			}
			return null;
		}
	}

}