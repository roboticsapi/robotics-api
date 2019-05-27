/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.fieldbus.ethernet.extension;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.driver.GenericInstantiator;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.fieldbus.ethernet.AbstractEthernetDriver;
import org.roboticsapi.runtime.fieldbus.ethernet.RealtimeEthernetDriver;

public final class SoftRobotRealtimeEthernetFieldbusExtension extends AbstractSoftRobotRoboticsBuilder {

	private final Map<AbstractEthernetDriver, GenericInstantiator> instantiatedDrivers = new HashMap<AbstractEthernetDriver, GenericInstantiator>();

	public SoftRobotRealtimeEthernetFieldbusExtension() {
		super(RealtimeEthernetDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "ethernet_rtnet" };
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
		if (roboticsObject instanceof RealtimeEthernetDriver) {
			final AbstractEthernetDriver driver = (AbstractEthernetDriver) roboticsObject;
			final GenericInstantiator instantiator = new GenericInstantiator(driver);

			instantiatedDrivers.put(driver, instantiator);
			driver.addOperationStateListener(instantiator);
		}
	}

	@Override
	protected void onRoboticsObjectUnavailable(RoboticsObject roboticsObject) {
		if (roboticsObject instanceof RealtimeEthernetDriver) {
			final RealtimeEthernetDriver driver = (RealtimeEthernetDriver) roboticsObject;
			final GenericInstantiator instantiator = instantiatedDrivers.remove(driver);
			driver.removeOperationStateListener(instantiator);
		}
	}

}
