/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.sensor.DoubleArraySensor;

public class MockDoubleArraySensor extends DoubleArraySensor {
	private boolean available = true;

	public MockDoubleArraySensor(RoboticsRuntime runtime, int size) {
		super(runtime, size);
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}
}
