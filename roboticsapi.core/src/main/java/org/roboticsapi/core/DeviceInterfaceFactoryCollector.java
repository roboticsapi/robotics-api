/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Map;

public final class DeviceInterfaceFactoryCollector {

	private final Map<Class<? extends DeviceInterface>, DeviceInterfaceFactory<?>> map;

	public DeviceInterfaceFactoryCollector(Map<Class<? extends DeviceInterface>, DeviceInterfaceFactory<?>> map) {
		this.map = map;
	}

	public <T extends DeviceInterface> void add(Class<T> type, DeviceInterfaceFactory<T> factory) {
		map.put(type, factory);
	}

}
