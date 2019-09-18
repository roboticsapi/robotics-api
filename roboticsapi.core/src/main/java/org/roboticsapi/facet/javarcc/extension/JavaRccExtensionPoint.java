/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.extension;

import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.devices.DeviceRegistry;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;

public interface JavaRccExtensionPoint {

	public interface DeviceFactory {
		public JDevice create(RpiParameters parameters, DeviceRegistry existingDevices) throws Exception;
	}

	public interface InterfaceExtractor {
		public RpiParameters getInterfaceParameters(JDevice device);
	}

	public void registerPrimitive(String name, Class<? extends JPrimitive> primitive);

	public void registerDevice(String type, DeviceFactory factory);

	public void registerInterface(String name, InterfaceExtractor extractor);

}
