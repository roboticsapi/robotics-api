/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.mockclass;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;

public class MockRealtimeRotation extends RealtimeRotation {
	private boolean available = true;

	public MockRealtimeRotation(RoboticsRuntime runtime) {
		super(runtime);
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}
}
