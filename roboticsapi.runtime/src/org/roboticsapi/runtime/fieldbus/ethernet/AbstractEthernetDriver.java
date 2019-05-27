/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.fieldbus.ethernet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.runtime.AbstractSoftRobotDeviceDriver;
import org.roboticsapi.runtime.driver.GenericLoadable;

public abstract class AbstractEthernetDriver extends AbstractSoftRobotDeviceDriver implements GenericLoadable {

	private int port;

	public AbstractEthernetDriver() {
		super();
	}

	public final int getPort() {
		return port;
	}

	@ConfigurationProperty(Optional = false)
	public final void setPort(int port) {
		immutableWhenInitialized();
		this.port = port;
	}

	@Override
	public abstract String getDeviceType();

	@Override
	public final boolean build() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("port", String.valueOf(getPort()));

		return loadDeviceDriver(parameters);
	}

	@Override
	public final void delete() {
		deleteDeviceDriver();
	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		return true; // TODO: No interfaces available until now
	}

}
