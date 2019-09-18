/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import java.util.Set;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.runtime.SingleDeviceActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Velocity;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.framework.cartesianmotion.action.MoveToCartesianGoal;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;

public class MoveToCartesianGoalActivityImpl extends SingleDeviceActivity {

	protected DeviceParameterBag parameters;
	protected MoveToCartesianGoal action;
	protected MotionCenterParameter mp;
	private final RealtimePose goal;

	public MoveToCartesianGoalActivityImpl(ActuatorDriver driver, RealtimePose goal, DeviceParameterBag parameters)
			throws RoboticsException {
		super("MoveToCartesianGoal", driver);
		this.goal = goal;
		this.parameters = parameters;

		getMotionCenter();

	}

	public MoveToCartesianGoalActivityImpl(String name, ActuatorDriver driver, RealtimePose goal,
			DeviceParameterBag parameters) throws RoboticsException {
		super(name, driver);
		this.goal = goal;
		this.parameters = parameters;

		getMotionCenter();
	}

	private void getMotionCenter() throws RoboticsException {
		mp = parameters.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new RoboticsException("MotionCenterParameter not given");
		}

		if (getGoal().getReference().getRelationsTo(mp.getMotionCenter().getReference()) == null) {
			throw new RoboticsException(
					"Goal frame (" + getGoal().getReference().getName() + ") and motion center frame ("
							+ mp.getMotionCenter().getReference().getName() + ") must be connected somehow");
		}
	}

	public RealtimePose getGoal() {
		return goal;
	}

	@Override
	protected Set<ActivityResult> getResultsForCommand(Command command) throws RoboticsException {

		Pose mc = mp.getMotionCenter();
		ActivityResult completionResult = completionResult(((RuntimeCommand) command).getCompletionResult(),
				new GoalPoseProperty(goal, mc),
				goal.isConstant() ? new CommandedPoseProperty(goal.getCurrentValue(), mc) : null,
				goal.isConstant()
						? new CommandedVelocityProperty(new Velocity(goal.getReference(), null, null, new Twist()), mc)
						: null);
		ActivityResult cancelResult = cancelResult(((RuntimeCommand) command).getCancelResult(),
				new GoalPoseProperty(goal, mc));

		return activityResultSet(completionResult, cancelResult);
	}

	@Override
	protected Command getCommandForResult(ActivityResult result) throws RoboticsException {
		return createRuntimeCommand(new MoveToCartesianGoal(getGoal()), parameters);
	}

}