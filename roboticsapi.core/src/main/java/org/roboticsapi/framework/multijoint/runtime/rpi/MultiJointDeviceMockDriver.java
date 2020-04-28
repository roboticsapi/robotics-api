/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi;

import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public final class MultiJointDeviceMockDriver<D extends MultiJointDevice> extends MultiJointDeviceGenericDriver<D> {

	private final static String DEVICE_TYPE = "multijoint_sim";

	@Override
	public String getRpiDeviceType() {
		return DEVICE_TYPE;
	}

}
