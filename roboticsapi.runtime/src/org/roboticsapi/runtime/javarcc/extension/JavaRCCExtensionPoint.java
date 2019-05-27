/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc.extension;

import java.util.Map;

import org.roboticsapi.runtime.core.javarcc.devices.DeviceRegistry;
import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public interface JavaRCCExtensionPoint {

	public interface DeviceFactory {
		public JDevice create(Map<String, String> parameters, DeviceRegistry existingDevices) throws Exception;
	}

	public void registerPrimitive(String name, Class<? extends JPrimitive> primitive);

	public void registerDevice(String type, DeviceFactory factory);

	public void registerInterface(String name, Class<? extends JDevice> type);

}
