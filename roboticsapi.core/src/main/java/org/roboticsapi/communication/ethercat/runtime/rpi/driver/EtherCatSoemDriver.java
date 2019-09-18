/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.communication.ethercat.runtime.rpi.driver;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;

public final class EtherCatSoemDriver extends EtherCatGenericDriver {

	private final Dependency<String> ethernetDevice = createDependency("ethernetDevice");

	public String getEthernetDevice() {
		return ethernetDevice.get();
	}

	public void setEthernetDevice(String ethernetDevice) {
		this.ethernetDevice.set(ethernetDevice);
	}

	@Override
	protected RpiParameters getRpiDeviceParameters() {
		return super.getRpiDeviceParameters().with("ethernetdevice", ethernetDevice.get());
	}

	@Override
	public String getRpiDeviceType() {
		return "ethercat";
	}
}
