/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.Test;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.mockclass.MockActuatorDriverImpl;
import org.roboticsapi.core.mockclass.MockActuatorImpl;
import org.roboticsapi.core.mockclass.MockRoboticsRuntime;

// Only unit tests
public class RoboticsRuntimeTest {
	private class InitializedMockActuator extends MockActuatorImpl {
		// Method isPresent() returns false
		@Override
		public boolean isPresent() {
			return false;
		}
	}

	private final MockRoboticsRuntime mockRuntime = new MockRoboticsRuntime();

	@Test(expected = RoboticsException.class)
	public void testCreateRuntimeCommandWithNotInitializedActuatorExpectingException() throws RoboticsException {
		Actuator mockActuator = new MockActuatorImpl();
		ActuatorDriver mockDriver = new MockActuatorDriverImpl(mockActuator, null);

		mockRuntime.createRuntimeCommand(mockDriver, null, new DeviceParameterBag());
	}
}
