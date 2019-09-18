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
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.framework.multijoint.activity.JointStopInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public class JointStopInterfaceImpl extends ActuatorInterfaceImpl implements JointStopInterface {

	public JointStopInterfaceImpl(MultiJointDeviceGenericDriver<?> driver) {
		super(driver);
	}

	@Override
	public Activity stopAndLook(DeviceParameters... parameters) throws RoboticsException {
		return new JointStopActivityImpl((MultiJointDeviceDriver) getDriver(),
				getDefaultParameters().withParameters(parameters));
	}

}
