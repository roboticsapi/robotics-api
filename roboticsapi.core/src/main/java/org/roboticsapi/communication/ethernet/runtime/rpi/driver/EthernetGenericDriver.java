/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.communication.ethernet.runtime.rpi.driver;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractCommunicationDriver;

public class EthernetGenericDriver extends AbstractCommunicationDriver {

	private final Dependency<Integer> port;

	public EthernetGenericDriver() {
		port = createDependency("port");
	}

	public final int getPort() {
		return port.get();
	}

	@ConfigurationProperty
	public final void setPort(int port) {
		this.port.set(port);
	}

	@Override
	protected RpiParameters getRpiDeviceParameters() {
		return super.getRpiDeviceParameters().with("port", new RPIint(port.get()));
	}

	@Override
	protected boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		return true;
	}

}
