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

public class TestActuatorDriver extends TestDeviceDriver implements ActuatorDriver {

	private final TestActuator actuator;

	public TestActuatorDriver(TestActuator actuator, RoboticsRuntime runtime) {
		super(actuator, runtime);
		this.actuator = actuator;
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
	public TestActuator getDevice() {
		return actuator;
	}

}
