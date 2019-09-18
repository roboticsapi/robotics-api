/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.activity;

import java.util.Arrays;
import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.action.WrappedAction;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.activity.runtime.PlannedSingleDeviceActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.action.JointMotion;
import org.roboticsapi.framework.multijoint.action.JointMotionPlan;
import org.roboticsapi.framework.multijoint.action.PTP;
import org.roboticsapi.framework.multijoint.action.PTPFromMotion;
import org.roboticsapi.framework.multijoint.parameter.BlendingParameter;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.framework.multijoint.property.CommandedPositionProperty;
import org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty;
import org.roboticsapi.framework.multijoint.property.GoalProperty;

public abstract class AbstractPtpActivityImpl extends PlannedSingleDeviceActivity implements PlannedActivity {
	private final double speedFactor;

	private final DeviceParameterBag parameters;
	private final MultiJointDevice robot;

	protected AbstractPtpActivityImpl(String name, ActuatorDriver driver, double speedFactor,
			DeviceParameterBag parameters) {
		super(name, driver);
		this.robot = (MultiJointDevice) driver.getDevice();
		this.speedFactor = speedFactor;
		this.parameters = parameters;
	}

	protected AbstractPtpActivityImpl(ActuatorDriver driver, double speedFactor, DeviceParameterBag parameters) {
		this(driver.getDevice().getName() + ".ptp()", driver, speedFactor, parameters);
	}

	@Override
	protected Command getCommandForResult(ActivityResult result) throws RoboticsException {

		if (result == null) {
			return createRegularPTP(robot.getJointAngles());
		}

		GoalProperty prevGoal = result.getMetadata(getDevice(), GoalProperty.class);

		CommandedPositionProperty cmdPos = result.getMetadata(getDevice(), CommandedPositionProperty.class);
		CommandedVelocityProperty cmdVel = result.getMetadata(getDevice(), CommandedVelocityProperty.class);

		if (cmdVel != null && cmdPos != null && prevGoal != null) {
			boolean velZero = true;
			for (int i = 0; i < robot.getJointCount(); i++) {
				if (Math.abs(cmdVel.getVelocity()[i]) > 1e-4) {
					velZero = false;
				}
			}

			if (velZero) {
				// standing still
				return createRegularPTP(prevGoal.getGoal());
			} else {
				// still moving
				return createBlendingPTP(prevGoal.getGoal(), cmdPos.getPosition(), cmdVel.getVelocity());
			}
		}

		return null;
	}

	protected JointMotion<?> findMotion(Command command) {
		if (command instanceof RuntimeCommand) {
			return findMotion(((RuntimeCommand) command).getAction());
		}
		return null;
	}

	protected JointMotion<?> findMotion(Action action) {
		if (action instanceof JointMotion<?>) {
			return (JointMotion<?>) action;
		} else if (action instanceof WrappedAction) {
			return findMotion(((WrappedAction) action).getWrappedAction());
		}
		return null;
	}

	private RealtimeBoolean calculateTakeoverAllowedState() {
		BlendingParameter blendingParameter = parameters.get(BlendingParameter.class);

		if (blendingParameter != null) {
			// event triggering start of blending
			if (blendingParameter.getAtProgress() < 1) {
				return getMotionTimeProgress(blendingParameter.getAtProgress());
			}
		}
		return null;
	}

	@Override
	protected Set<ActivityResult> getResultsForCommand(Command command) throws RoboticsException {
		RuntimeCommand c = (RuntimeCommand) command;
		JointMotion<?> motion = findMotion(command);
		JointMotionPlan plan = c.getPlan(motion);

		GoalProperty goal = new GoalProperty(plan.getJointPositionsAt(plan.getTotalTime()));

		CommandedPositionProperty pose = new CommandedPositionProperty(plan.getJointPositionsAt(plan.getTotalTime()));
		CommandedVelocityProperty vel = new CommandedVelocityProperty(new double[robot.getJointCount()]);

		ActivityResult completed = completionResult(c.getCompletionResult(), goal, pose, vel);
		ActivityResult cancelled = cancelResult(c.getCancelResult(), goal);

		RealtimeBoolean blendState = calculateTakeoverAllowedState();
		if (blendState != null) {
			blendState = blendState.getForScope(command);
		}

		if (blendState != null) {
			Double blendTime = plan.getTimeAtFirstOccurence(blendState);
			pose = new CommandedPositionProperty(plan.getJointPositionsAt(blendTime));
			vel = new CommandedVelocityProperty(plan.getJointVelocitiesAt(blendTime));

			ActivityResult blending = takeoverResult(
					c.addTakeoverResult("Blending", blendState.and(c.getCancelState().not()), false), goal, pose, vel);

			return activityResultSet(completed, blending, cancelled);
		} else {
			return activityResultSet(completed, cancelled);
		}
	}

	protected RuntimeCommand createRegularPTP(double[] start) throws RoboticsException {
		double[] goal = getGoal(start);
		JointDeviceParameters deviceParameters = parameters.get(JointDeviceParameters.class);
		for (int i = 0; i < goal.length; i++) {
			if (goal[i] != goal[i] || goal[i] < deviceParameters.getJointParameters(i).getMinimumPosition()
					|| goal[i] > deviceParameters.getJointParameters(i).getMaximumPosition()) {
				throw new RoboticsException("Unreachable goal for PTP (joint " + i + "): " + Arrays.toString(goal));
			}
		}
		PTP regularPTP = new PTP(speedFactor, start, goal);

		return createRuntimeCommand(regularPTP, parameters);

	}

	protected RuntimeCommand createBlendingPTP(double[] start, double[] pos, double[] vel) throws RoboticsException {
		PTPFromMotion blendedPTP = new PTPFromMotion(speedFactor, start, getGoal(start), pos, vel);
		// JointErrorCorrection corrected = new JointErrorCorrection(blendedPTP,
		// robot.getJoints(), pos);
		return createRuntimeCommand(blendedPTP, parameters);
	}

	protected abstract double[] getGoal(double[] start) throws RoboticsException;

}