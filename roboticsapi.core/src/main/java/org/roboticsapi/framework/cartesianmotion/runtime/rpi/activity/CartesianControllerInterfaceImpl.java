/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.activity.runtime.FromCommandActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.cartesianmotion.action.SwitchCartesianController;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianControllerInterface;
import org.roboticsapi.framework.cartesianmotion.controller.CartesianController;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;

/**
 * Simple implementation of {@link CartesianControllerInterface} that relies on
 * a "raw" {@link SwitchCartesianController} action.
 */
public class CartesianControllerInterfaceImpl extends ActuatorInterfaceImpl implements CartesianControllerInterface {

	public CartesianControllerInterfaceImpl(CartesianActuatorDriver driver) {
		super(driver);
	}

	@Override
	public Activity switchCartesianController(CartesianController controller, DeviceParameters... parameters)
			throws RoboticsException {
		return new FromCommandActivity(() -> createRuntimeCommand(new SwitchCartesianController(controller),
				getDefaultParameters().withParameters(parameters)));
	}

}
