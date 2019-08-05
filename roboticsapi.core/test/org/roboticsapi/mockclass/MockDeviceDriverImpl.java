/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsRuntime;

public class MockDeviceDriverImpl extends MockOnlineObjectImpl implements DeviceDriver {
	private final RoboticsRuntime runtime;

	public MockDeviceDriverImpl(RoboticsRuntime runtime) {
		this.runtime = runtime;
		setName("MockDeviceDriver");
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}
}
