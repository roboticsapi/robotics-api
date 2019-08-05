/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.sensor.VelocitySensor;

public class MockVelocitySensor extends VelocitySensor {
	private boolean available = true;

	public MockVelocitySensor(RoboticsRuntime runtime, Frame movingFrame, Frame referenceFrame, Point pivotPoint,
			Orientation orientation) {
		super(runtime, movingFrame, referenceFrame, pivotPoint, orientation);
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}
}
