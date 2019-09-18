/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import java.util.List;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public class MockActuatorDriverImpl extends MockDeviceDriverImpl implements ActuatorDriver {

	public MockActuatorDriverImpl(Device device, RoboticsRuntime runtime) {
		super(device, runtime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Actuator getDevice() {
		// TODO Auto-generated method stub
		return null;
	}

}
