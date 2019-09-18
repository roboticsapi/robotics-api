/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import java.util.Set;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.runtime.SingleDeviceActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.framework.cartesianmotion.action.FollowCartesianGoal;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;

public class FollowCartesianGoalActivityImpl extends SingleDeviceActivity {

	private final DeviceParameterBag parameters;
	private RealtimePose goal;
	private final Pose mc;

	public FollowCartesianGoalActivityImpl(ActuatorDriver driver, RealtimePose goal, DeviceParameterBag parameters)
			throws RoboticsException {
		super("Follow Cartesian goal", driver);
		this.goal = goal;
		this.parameters = parameters;

		MotionCenterParameter motionCenterParameter = parameters.get(MotionCenterParameter.class);
		if (motionCenterParameter == null) {
			throw new RoboticsException("MotionCenterParameter not given");
		}

		mc = motionCenterParameter.getMotionCenter();

		checkGoal();
	}

	protected void checkGoal() throws RoboticsException {
		if (goal == null) {
			return;
		}

		if (goal.getReference().getRelationsTo(mc.getReference()) == null) {
			throw new RoboticsException("Goal Frame (" + goal.getReference().getName() + ") and motion center frame ("
					+ mc.getReference().getName() + ") must be connected somehow");
		}
	}

	protected void setGoal(RealtimePose goal) throws RoboticsException {
		this.goal = goal;

		checkGoal();
	}

	protected DeviceParameterBag getParameters() {
		return parameters;
	}

	@Override
	protected Set<ActivityResult> getResultsForCommand(Command command) throws RoboticsException {
		ActivityResult takeoverResult = takeoverResult(
				command.addTakeoverResult("Reached", ((Actuator) getDevice()).getCompletedState(command), false),
				new GoalPoseProperty(goal, mc),
				goal.isConstant() ? new CommandedPoseProperty(goal.getCurrentValue(), mc) : null);
		ActivityResult cancelResult = completionResult(((RuntimeCommand) command).getCancelResult(),
				new GoalPoseProperty(goal, mc));

		return activityResultSet(takeoverResult, cancelResult);
	}

	@Override
	protected Command getCommandForResult(ActivityResult result) throws RoboticsException {

		RuntimeCommand goalcommand = createRuntimeCommand(new FollowCartesianGoal(goal), getParameters());

		return goalcommand;
	}

}
