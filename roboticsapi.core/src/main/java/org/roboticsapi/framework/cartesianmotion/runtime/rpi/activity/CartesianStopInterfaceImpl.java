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
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianStopInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;

public class CartesianStopInterfaceImpl extends ActuatorInterfaceImpl implements CartesianStopInterface {

	private CartesianActuatorDriver driver;

	public CartesianStopInterfaceImpl(CartesianActuatorDriver driver) {
		super(driver);
		this.driver = driver;
	}

	@Override
	public Activity stopAndLook(DeviceParameters... parameters) throws RoboticsException {
		return new CartesianStopActivityImpl(driver, driver.getDevice().getReferenceFrame(),
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public Activity stopAndLook(Frame reference, DeviceParameters... parameters) throws RoboticsException {
		return new CartesianStopActivityImpl(driver, reference, getDefaultParameters().withParameters(parameters));
	}

}
