/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.fieldbus.canopen.extension;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.driver.GenericInstantiator;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.fieldbus.canopen.CanOpenFieldbusDriver;

public final class SoftRobotCanOpenFieldbusExtension extends AbstractSoftRobotRoboticsBuilder {

	private final Map<CanOpenFieldbusDriver, GenericInstantiator> instantiatedDrivers = new HashMap<CanOpenFieldbusDriver, GenericInstantiator>();

	public SoftRobotCanOpenFieldbusExtension() {
		super(CanOpenFieldbusDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "canopen" };
	}

	@Override
	protected void onRuntimeAvailable(SoftRobotRuntime runtime) {
		// empty...
	}

	@Override
	protected void onRuntimeUnavailable(SoftRobotRuntime runtime) {
		// empty...
	}

	@Override
	protected void onRoboticsObjectAvailable(RoboticsObject roboticsObject) {
		if (roboticsObject instanceof CanOpenFieldbusDriver) {
			final CanOpenFieldbusDriver driver = (CanOpenFieldbusDriver) roboticsObject;
			final GenericInstantiator instantiator = new GenericInstantiator(driver);

			instantiatedDrivers.put(driver, instantiator);
			driver.addOperationStateListener(instantiator);
		}
	}

	@Override
	protected void onRoboticsObjectUnavailable(RoboticsObject roboticsObject) {
		if (roboticsObject instanceof CanOpenFieldbusDriver) {
			final CanOpenFieldbusDriver driver = (CanOpenFieldbusDriver) roboticsObject;
			final GenericInstantiator instantiator = instantiatedDrivers.remove(driver);

			driver.removeOperationStateListener(instantiator);
		}
	}

}
