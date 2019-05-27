/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public class CartesianGoalMotionInterfaceImpl<T extends CartesianMotionDevice> extends ActuatorInterfaceImpl<T>
		implements CartesianGoalMotionInterface {

	public CartesianGoalMotionInterfaceImpl(T device) {
		super(device);
	}

	@Override
	public FollowCartesianGoalFromJavaActivity followCartesianGoalFromJava(Frame reference,
			DeviceParameters... parameters) throws RoboticsException {
		return new FollowCartesianGoalFromJavaActivityImpl(getDevice(), reference,
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public RtActivity followCartesianGoal(final Frame goal, DeviceParameters... parameters) throws RoboticsException {

		return new FollowCartesianGoalActivityImpl(getDevice(), goal,
				getDefaultParameters().withParameters(parameters));

	}

	@Override
	public RtActivity moveToCartesianGoal(final Frame goal, DeviceParameters... parameters) throws RoboticsException {

		return new MoveToCartesianGoalActivityImpl(getDevice(), goal,
				getDefaultParameters().withParameters(parameters));
	}

}
