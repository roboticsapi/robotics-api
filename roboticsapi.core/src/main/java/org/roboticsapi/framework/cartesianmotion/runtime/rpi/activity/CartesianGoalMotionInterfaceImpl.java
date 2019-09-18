/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianGoalMotionInterface;

public class CartesianGoalMotionInterfaceImpl extends ActuatorInterfaceImpl implements CartesianGoalMotionInterface {

	public CartesianGoalMotionInterfaceImpl(ActuatorDriver driver) {
		super(driver);
	}

	@Override
	public Activity followCartesianGoal(final Pose goal, DeviceParameters... parameters) throws RoboticsException {
		return new FollowCartesianGoalActivityImpl(getDriver(), goal.asRealtimeValue(),
				getDefaultParameters().withParameters(parameters));

	}

	@Override
	public Activity followCartesianGoal(final RealtimePose goal, DeviceParameters... parameters)
			throws RoboticsException {
		return new FollowCartesianGoalActivityImpl(getDriver(), goal,
				getDefaultParameters().withParameters(parameters));

	}

	@Override
	public Activity moveToCartesianGoal(final Pose goal, DeviceParameters... parameters) throws RoboticsException {
		return new MoveToCartesianGoalActivityImpl(getDriver(), goal.asRealtimeValue(),
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public Activity moveToCartesianGoal(final RealtimePose goal, DeviceParameters... parameters)
			throws RoboticsException {
		return new MoveToCartesianGoalActivityImpl(getDriver(), goal,
				getDefaultParameters().withParameters(parameters));
	}
}
