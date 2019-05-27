/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.FromCommandActivity;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.action.HoldJointPosition;

public class JointHoldMotionInterfaceImpl extends ActuatorInterfaceImpl<MultiJointDevice>
		implements JointHoldMotionInterface {

	public JointHoldMotionInterfaceImpl(MultiJointDevice device) {
		super(device);
	}

	@Override
	public RtActivity holdJointPosition(DoubleSensor[] sensors, DeviceParameters... parameters)
			throws RoboticsException {
		if (sensors != null && sensors.length != getDevice().getJointCount()) {
			throw new RoboticsException("Expected " + getDevice().getJointCount()
					+ " DoubleSensors (one for each device axis), but " + sensors.length + " were provided");
		}

		RuntimeCommand command = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(),
				new HoldJointPosition(sensors), getDefaultParameters().withParameters(parameters));

		return new FromCommandActivity(command, getDevice());
	}

}
