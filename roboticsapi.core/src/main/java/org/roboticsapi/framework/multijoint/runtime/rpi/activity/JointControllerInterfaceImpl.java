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
import org.roboticsapi.framework.multijoint.action.SwitchJointController;
import org.roboticsapi.framework.multijoint.activity.JointControllerInterface;
import org.roboticsapi.framework.multijoint.controller.JointController;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

/**
 * Simple implementation of @{link {@link JointControllerInterface} that relies
 * on a "raw" {@link SwitchJointController} action.
 */
public class JointControllerInterfaceImpl extends ActuatorInterfaceImpl implements JointControllerInterface {

	public JointControllerInterfaceImpl(MultiJointDeviceGenericDriver<?> driver) {
		super(driver);
	}

	@Override
	public Activity switchJointController(JointController controller, DeviceParameters... parameters)
			throws RoboticsException {
		return new FromCommandActivity(() -> createRuntimeCommand(new SwitchJointController(controller),
				getDefaultParameters().withParameters(parameters)));
	}

}
