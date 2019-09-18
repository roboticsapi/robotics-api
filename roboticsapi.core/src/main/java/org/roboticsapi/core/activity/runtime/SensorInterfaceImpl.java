/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.runtime;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.SensorInterface;

public class SensorInterfaceImpl implements SensorInterface {

	private final Device sensor;
	private final RoboticsRuntime runtime;

	public SensorInterfaceImpl(Device sensor, RoboticsRuntime runtime) {
		this.sensor = sensor;
		this.runtime = runtime;

	}

	@Override
	public Device getDevice() {
		return sensor;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}

}
