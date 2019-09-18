/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.devices;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.roboticsapi.core.util.RAPILogger;

public class DeviceRegistry {
	private Map<String, JDevice> devices = new HashMap<String, JDevice>();
	private Map<String, String> deviceTypes = new HashMap<String, String>();
	private DeviceRegistryListener listener;

	public interface DeviceRegistryListener {
		void deviceAdded(String name);

		void deviceRemoved(String name);

		void deviceChanged(String name);
	}

	public DeviceRegistry(DeviceRegistryListener listener) {
		this.listener = listener;
	}

	public <T extends JDevice> T get(String name, Class<T> type) {
		try {
			return type.cast(devices.get(name));
		} catch (ClassCastException e) {
			return null;
		}
	}

	public Map<String, String> list() {
		return deviceTypes;
	}

	public void register(String name, String type, JDevice device) {
		devices.put(name, device);
		deviceTypes.put(name, type);
		listener.deviceAdded(name);
	}

	public void remove(String name) {
		JDevice device = devices.get(name);
		try {
			device.destroy();
		} catch (Exception e) {
			RAPILogger.logException(device, e);
		}
		devices.remove(name);
		listener.deviceRemoved(name);
	}

	public void notifyChanged(JDevice device) {
		for (Entry<String, JDevice> entry : devices.entrySet()) {
			if (entry.getValue() == device)
				listener.deviceChanged(entry.getKey());
		}
	}

}
