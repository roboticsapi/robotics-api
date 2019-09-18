/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Map;

public final class DeviceInterfaceModifierCollector {

	private final Map<Class<? extends DeviceInterface>, DeviceInterfaceModifier<?>> map;

	public DeviceInterfaceModifierCollector(Map<Class<? extends DeviceInterface>, DeviceInterfaceModifier<?>> map) {
		this.map = map;
	}

	public <T extends DeviceInterface> void add(Class<T> type, DeviceInterfaceModifier<T> factory) {
		map.put(type, factory);
	}

}
