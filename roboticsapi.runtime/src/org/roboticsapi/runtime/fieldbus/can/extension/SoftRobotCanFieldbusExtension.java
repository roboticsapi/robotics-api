/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.fieldbus.can.extension;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.driver.GenericInstantiator;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.fieldbus.can.CanFieldbusDriver;
import org.roboticsapi.runtime.fieldbus.can.RtSocketCanFieldbusDriver;
import org.roboticsapi.runtime.fieldbus.can.SocketCanFieldbusDriver;

public final class SoftRobotCanFieldbusExtension extends AbstractSoftRobotRoboticsBuilder {

	private final Map<CanFieldbusDriver, GenericInstantiator> instantiatedDrivers = new HashMap<CanFieldbusDriver, GenericInstantiator>();

	public SoftRobotCanFieldbusExtension() {
		super(RtSocketCanFieldbusDriver.class, SocketCanFieldbusDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "socketcan", "rtsocketcan" };
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
		if (roboticsObject instanceof CanFieldbusDriver) {
			final CanFieldbusDriver driver = (CanFieldbusDriver) roboticsObject;
			final GenericInstantiator instantiator = new GenericInstantiator(driver);

			instantiatedDrivers.put(driver, instantiator);
			driver.addOperationStateListener(instantiator);
		}
	}

	@Override
	protected void onRoboticsObjectUnavailable(RoboticsObject roboticsObject) {
		if (roboticsObject instanceof CanFieldbusDriver) {
			final CanFieldbusDriver driver = (CanFieldbusDriver) roboticsObject;
			final GenericInstantiator instantiator = instantiatedDrivers.remove(driver);

			driver.removeOperationStateListener(instantiator);
		}
	}

}
