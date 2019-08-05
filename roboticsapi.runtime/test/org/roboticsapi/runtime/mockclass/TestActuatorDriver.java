/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public class TestActuatorDriver extends TestDeviceDriver implements ActuatorDriver {

	public TestActuatorDriver(RoboticsRuntime runtime) {
		super(runtime);
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		List<ActuatorDriverRealtimeException> actuatorExceptions = new ArrayList<ActuatorDriverRealtimeException>();

		actuatorExceptions.add(new TestActuatorException(this));
		actuatorExceptions.add(new TestActuatorExceptionSubclass(this));

		return actuatorExceptions;
	}

	@Override
	public final List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes() {
		List<Class<? extends ActuatorDriverRealtimeException>> types = new ArrayList<Class<? extends ActuatorDriverRealtimeException>>();

		for (ActuatorDriverRealtimeException e : defineActuatorDriverExceptions()) {
			types.add(e.getClass());
		}

		return types;
	}

	@Override
	public final <T extends ActuatorDriverRealtimeException> T defineActuatorDriverException(Class<T> type)
			throws CommandException {
		for (ActuatorDriverRealtimeException ex : defineActuatorDriverExceptions()) {
			if (type.isAssignableFrom(ex.getClass())) {
				return type.cast(ex);
			}
		}
		throw new CommandException("Found no actuator exception of the given type: " + type.getName());
	}

}
