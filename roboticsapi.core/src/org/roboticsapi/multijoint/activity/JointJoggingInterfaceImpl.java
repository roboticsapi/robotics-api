/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.MultiJointDevice;

public class JointJoggingInterfaceImpl extends ActuatorInterfaceImpl<MultiJointDevice>
		implements JointJoggingInterface {

	public JointJoggingInterfaceImpl(MultiJointDevice multiJointDevice) {
		super(multiJointDevice);
	}

	@Override
	public JointJoggingActivity jointJogging(DeviceParameters... parameters) throws RoboticsException {
		return new JointJoggingActivityImpl(getDevice(), getDefaultParameters().withParameters(parameters));
	}
}
