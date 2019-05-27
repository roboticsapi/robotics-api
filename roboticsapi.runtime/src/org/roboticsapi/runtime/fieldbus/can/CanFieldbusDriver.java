/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.fieldbus.can;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.runtime.AbstractSoftRobotDeviceDriver;
import org.roboticsapi.runtime.driver.GenericLoadable;

/**
 * This (abstract) class represent a CAN fieldbus device at the runtime.
 */
public abstract class CanFieldbusDriver extends AbstractSoftRobotDeviceDriver implements GenericLoadable {

	private String communicationDevice;

	protected CanFieldbusDriver() {
		super();
	}

	public final String getCommunicationDevice() {
		return communicationDevice;
	}

	@ConfigurationProperty(Optional = false)
	public final void setCommunicationDevice(String communicationDevice) {
		immutableWhenInitialized();
		this.communicationDevice = communicationDevice;
	}

	@Override
	public String getDeviceType() {
		return getCanDeviceType();
	}

	@Override
	public final boolean build() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("devicename", getCommunicationDevice());

		return loadDeviceDriver(parameters);
	}

	@Override
	public final void delete() {
		deleteDeviceDriver();
	}

	protected abstract String getCanDeviceType();

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		return true; // TODO: No interfaces available until now
	}

}
