/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

/**
 * A bag containing various device parameters
 */
public class DeviceParameterBag extends ParameterBag<DeviceParameters> {

	@Override
	protected boolean isElementAssignableFrom(Class<?> target) {
		return DeviceParameters.class.isAssignableFrom(target);
	}

	@Override
	public DeviceParameterBag withParameters(DeviceParameters... additionalDeviceParameters) {
		return (DeviceParameterBag) super.withParameters(additionalDeviceParameters);
	}

	@Override
	public DeviceParameterBag withParameters(ParameterBag<DeviceParameters> additionalParameters) {
		return (DeviceParameterBag) super.withParameters(additionalParameters);
	}

	@Override
	protected ParameterBag<DeviceParameters> createNew() {
		return new DeviceParameterBag();
	}

	@Override
	public DeviceParameters[] getArray() {
		List<DeviceParameters> all = getAll();

		return all.toArray(new DeviceParameters[all.size()]);
	}

}
