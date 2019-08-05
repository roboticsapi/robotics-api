/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.List;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public class MockActuatorDriverImpl extends MockDeviceDriverImpl implements ActuatorDriver {
	public MockActuatorDriverImpl(RoboticsRuntime runtime) {
		super(runtime);
		setName("MockActuatorDriver");
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		return null;
	}

	@Override
	public <T extends ActuatorDriverRealtimeException> T defineActuatorDriverException(Class<T> type)
			throws CommandException {
		return null;
	}

	@Override
	public List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes() {
		return null;
	}
}
