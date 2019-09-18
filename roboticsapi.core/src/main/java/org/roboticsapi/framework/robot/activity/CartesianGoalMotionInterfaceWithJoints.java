/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianGoalMotionInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;

public class CartesianGoalMotionInterfaceWithJoints
		extends CartesianMotionWithJointMetadata<CartesianGoalMotionInterface> implements CartesianGoalMotionInterface {

	public CartesianGoalMotionInterfaceWithJoints(CartesianGoalMotionInterface instance, CartesianMotionDevice device) {
		super(instance);
	}

	@Override
	public Activity followCartesianGoal(Pose goal, DeviceParameters... parameters) throws RoboticsException {
		return instance.followCartesianGoal(goal, parameters);
	}

	@Override
	public Activity followCartesianGoal(RealtimePose goal, DeviceParameters... parameters) throws RoboticsException {
		return instance.followCartesianGoal(goal, parameters);
	}

	@Override
	public Activity moveToCartesianGoal(Pose goal, DeviceParameters... parameters) throws RoboticsException {
		Activity ret = instance.moveToCartesianGoal(goal, parameters);
		addProviders(ret);
		return ret;
	}

	@Override
	public Activity moveToCartesianGoal(RealtimePose goal, DeviceParameters... parameters) throws RoboticsException {
		Activity ret = instance.moveToCartesianGoal(goal, parameters);
		addProviders(ret);
		return ret;
	}

}
