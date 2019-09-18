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
import org.roboticsapi.core.activity.runtime.FromCommandActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.framework.cartesianmotion.action.FollowCartesianVelocity;
import org.roboticsapi.framework.cartesianmotion.action.HoldCartesianVelocity;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianVelocityMotionInterface;

public class CartesianVelocityMotionInterfaceImpl extends ActuatorInterfaceImpl
		implements CartesianVelocityMotionInterface {

	public CartesianVelocityMotionInterfaceImpl(ActuatorDriver driver) {
		super(driver);
	}

	@Override
	public Activity holdVelocity(RealtimeVelocity velocity, DeviceParameters... parameters) throws RoboticsException {
		HoldCartesianVelocity action = new HoldCartesianVelocity(velocity);
		return new FromCommandActivity(
				() -> createRuntimeCommand(action, getDefaultParameters().withParameters(parameters)), getDevice());
	}

	@Override
	public Activity followVelocity(RealtimeVelocity velocity, DeviceParameters... parameters) throws RoboticsException {
		FollowCartesianVelocity action = new FollowCartesianVelocity(velocity);
		return new FromCommandActivity(
				() -> createRuntimeCommand(action, getDefaultParameters().withParameters(parameters)), getDevice());
	}

}
