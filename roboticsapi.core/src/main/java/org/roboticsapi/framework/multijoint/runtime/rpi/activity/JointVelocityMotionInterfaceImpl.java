/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.activity.runtime.FromCommandActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.multijoint.action.FollowJointVelocity;
import org.roboticsapi.framework.multijoint.action.HoldJointVelocity;
import org.roboticsapi.framework.multijoint.activity.JointVelocityMotionInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public class JointVelocityMotionInterfaceImpl extends ActuatorInterfaceImpl implements JointVelocityMotionInterface {

	public JointVelocityMotionInterfaceImpl(MultiJointDeviceGenericDriver<?> driver) {
		super(driver);
	}

	@Override
	public Activity holdVelocity(RealtimeDouble[] velocity, DeviceParameters... parameters) throws RoboticsException {
		HoldJointVelocity action = new HoldJointVelocity(velocity);
		return new FromCommandActivity(
				() -> createRuntimeCommand(action, getDefaultParameters().withParameters(parameters)), getDevice());
	}

	@Override
	public Activity followVelocity(RealtimeDouble[] velocity, DeviceParameters... parameters) throws RoboticsException {
		FollowJointVelocity action = new FollowJointVelocity(velocity);
		return new FromCommandActivity(
				() -> createRuntimeCommand(action, getDefaultParameters().withParameters(parameters)), getDevice());
	}

}
