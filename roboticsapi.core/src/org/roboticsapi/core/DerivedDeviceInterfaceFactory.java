/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.Device.InterfaceFactoryListener;

public abstract class DerivedDeviceInterfaceFactory<T extends DeviceInterface> extends SingleDeviceInterfaceFactory<T>
		implements InterfaceFactoryListener {
	boolean isCompleted = false;
	private final Class<?>[] requiredInterfaces;
	private final Map<Class<?>, DeviceInterfaceFactory> interfaceFactories = new HashMap<Class<?>, DeviceInterfaceFactory>();

	public DerivedDeviceInterfaceFactory(Class<?>... requiredInterfaces) {
		this.requiredInterfaces = requiredInterfaces;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void interfaceFactoryAdded(Device device, DeviceInterfaceFactory factory) {
		if (isCompleted) {
			return;
		}
		boolean hasAll = true;
		for (Class<?> di : requiredInterfaces) {
			if (factory.canBuild((Class<? extends DeviceInterface>) di)) {
				interfaceFactories.put(di, factory);
			}
			if (!interfaceFactories.containsKey(di)) {
				hasAll = false;
			}
		}
		if (hasAll) {
			device.removeInterfaceFactoryListener(this);
			device.addInterfaceFactory(this);
			isCompleted = true;
		}
	}

	public final <I extends DeviceInterface> I use(Class<I> deviceInterface) {
		if (!interfaceFactories.containsKey(deviceInterface)) {
			return null;
		}
		return interfaceFactories.get(deviceInterface).build(deviceInterface);
	}

}
