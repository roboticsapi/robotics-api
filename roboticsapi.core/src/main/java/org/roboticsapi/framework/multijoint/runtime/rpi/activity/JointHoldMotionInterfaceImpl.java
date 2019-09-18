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
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.action.HoldJointPosition;
import org.roboticsapi.framework.multijoint.activity.JointHoldMotionInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public class JointHoldMotionInterfaceImpl extends ActuatorInterfaceImpl implements JointHoldMotionInterface {
	private final MultiJointDevice robot;

	public JointHoldMotionInterfaceImpl(MultiJointDeviceGenericDriver<?> driver) {
		super(driver);
		this.robot = driver.getDevice();
	}

	@Override
	public Activity holdJointPosition(RealtimeDouble[] sensors, DeviceParameters... parameters)
			throws RoboticsException {
		if (sensors != null && sensors.length != robot.getJointCount()) {
			throw new RoboticsException("Expected " + robot.getJointCount()
					+ " RealtimeDoubles (one for each device axis), but " + sensors.length + " were provided");
		}

		return new FromCommandActivity(() -> createRuntimeCommand(new HoldJointPosition(sensors),
				getDefaultParameters().withParameters(parameters)), getDevice());
	}

}
