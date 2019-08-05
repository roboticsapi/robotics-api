/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsRuntime;

public class TestDeviceDriver extends AbstractRoboticsObject implements DeviceDriver {

	RoboticsRuntime runtime;

	public TestDeviceDriver(RoboticsRuntime runtime) {
		this.runtime = runtime;
	}

	@Override
	public void addOperationStateListener(OperationStateListener listener) {
	}

	@Override
	public void removeOperationStateListener(OperationStateListener listener) {
	}

	@Override
	public OperationState getState() {
		return OperationState.OPERATIONAL;
	}

	@Override
	public boolean isPresent() {
		return false;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}

}
