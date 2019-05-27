/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.fieldbus.ethercat.extension;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.driver.GenericInstantiator;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.fieldbus.ethercat.EthercatDriver;

public final class SoftRobotEthercatFieldbusExtension extends AbstractSoftRobotRoboticsBuilder {

	private final Map<EthercatDriver, GenericInstantiator> instantiatedDrivers = new HashMap<EthercatDriver, GenericInstantiator>();

	public SoftRobotEthercatFieldbusExtension() {
		super(EthercatDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "ethercat" };
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
		if (roboticsObject instanceof EthercatDriver) {
			final EthercatDriver driver = (EthercatDriver) roboticsObject;
			final GenericInstantiator instantiator = new GenericInstantiator(driver);

			instantiatedDrivers.put(driver, instantiator);
			driver.addOperationStateListener(instantiator);
		}
	}

	@Override
	protected void onRoboticsObjectUnavailable(RoboticsObject roboticsObject) {
		if (roboticsObject instanceof EthercatDriver) {
			final EthercatDriver driver = (EthercatDriver) roboticsObject;
			final GenericInstantiator instantiator = instantiatedDrivers.remove(driver);
			driver.removeOperationStateListener(instantiator);
		}
	}

}
