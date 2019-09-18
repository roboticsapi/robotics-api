/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public class TestDeviceDriver extends AbstractRoboticsObject implements ActuatorDriver {

	RoboticsRuntime runtime;
	private final TestActuator device;

	public TestDeviceDriver(TestActuator device, RoboticsRuntime runtime) {
		this.device = device;
		this.runtime = runtime;
	}

	@Override
	public void addOperationStateListener(OperationStateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeOperationStateListener(OperationStateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public OperationState getState() {
		return OperationState.OPERATIONAL;
	}

	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}

	@Override
	public TestActuator getDevice() {
		return device;
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		return new ArrayList<ActuatorDriverRealtimeException>();
	}

	@Override
	public List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes() {
		return new ArrayList<Class<? extends ActuatorDriverRealtimeException>>();
	}

}
