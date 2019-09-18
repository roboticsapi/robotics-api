/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsRuntime;

public class MockDeviceDriverImpl extends MockOnlineObjectImpl implements DeviceDriver {
	private final RoboticsRuntime runtime;
	private final Device device;

	public MockDeviceDriverImpl(Device device, RoboticsRuntime runtime) {
		this.device = device;
		this.runtime = runtime;
		setName("MockDeviceDriver");
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}

	@Override
	public Device getDevice() {
		return device;
	}
}
