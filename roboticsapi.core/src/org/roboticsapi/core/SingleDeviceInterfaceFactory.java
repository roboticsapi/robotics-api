/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleDeviceInterfaceFactory<T extends DeviceInterface> implements DeviceInterfaceFactory {

	@Override
	public boolean canBuild(Class<? extends DeviceInterface> clazz) {
		return clazz.isAssignableFrom(build().getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends DeviceInterface> U build(Class<U> clazz) {
		return (U) build();
	}

	@Override
	public List<Class<? extends DeviceInterface>> getProvidedInterfaces() {
		List<Class<? extends DeviceInterface>> result = new ArrayList<Class<? extends DeviceInterface>>();

		result.add(build().getClass());

		return result;
	}

	protected abstract T build();

}
