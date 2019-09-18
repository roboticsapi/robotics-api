/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianGoalMotionInterface;
import org.roboticsapi.framework.multijoint.activity.JointGoalMotionInterface;
import org.roboticsapi.framework.robot.activity.GoalMotionInterface;
import org.roboticsapi.framework.robot.runtime.rpi.driver.RobotArmGenericDriver;

public class GoalMotionInterfaceImpl extends ActuatorInterfaceImpl implements GoalMotionInterface {

	public GoalMotionInterfaceImpl(RobotArmGenericDriver<?> driver) {
		super(driver);
	}

	@Override
	public Activity followCartesianGoal(Pose goal, DeviceParameters... parameters) throws RoboticsException {
		return use(CartesianGoalMotionInterface.class).followCartesianGoal(goal, parameters);
	}

	@Override
	public Activity followCartesianGoal(RealtimePose goal, DeviceParameters... parameters) throws RoboticsException {
		return use(CartesianGoalMotionInterface.class).followCartesianGoal(goal, parameters);
	}

	@Override
	public Activity moveToCartesianGoal(Pose goal, DeviceParameters... parameters) throws RoboticsException {
		return use(CartesianGoalMotionInterface.class).moveToCartesianGoal(goal, parameters);
	}

	@Override
	public Activity moveToCartesianGoal(RealtimePose goal, DeviceParameters... parameters) throws RoboticsException {
		return use(CartesianGoalMotionInterface.class).moveToCartesianGoal(goal, parameters);
	}

	@Override
	public Activity followJointGoal(RealtimeDouble[] goal, DeviceParameters... parameters) throws RoboticsException {
		return use(JointGoalMotionInterface.class).followJointGoal(goal, parameters);
	}

	@Override
	public Activity moveToJointGoal(RealtimeDouble[] goal, DeviceParameters... parameters) throws RoboticsException {
		return use(JointGoalMotionInterface.class).moveToJointGoal(goal, parameters);
	}

	@Override
	public Activity moveToJointGoal(double[] goal, DeviceParameters... parameters) throws RoboticsException {
		return use(JointGoalMotionInterface.class).moveToJointGoal(goal, parameters);
	}

}
