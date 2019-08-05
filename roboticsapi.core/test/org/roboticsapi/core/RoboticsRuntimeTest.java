/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.mockclass.MockActuatorImpl;
import org.roboticsapi.mockclass.MockRoboticsRuntime;

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

	@Test
	public void testSetDefaultOverride() {
		double testOverride = (mockRuntime.getDefaultOverride() == 1) ? 2 : 1;

		mockRuntime.setDefaultOverride(testOverride);

		assertEquals(testOverride, mockRuntime.getDefaultOverride(), 0.001);
	}

	@Test(expected = RoboticsException.class)
	public void testCreateRuntimeCommandWithNotInitializedActuatorExpectingException() throws RoboticsException {
		Actuator mockActuator = new MockActuatorImpl();

		mockRuntime.createRuntimeCommand(mockActuator, null, new DeviceParameterBag());
	}

	@Test(expected = RoboticsException.class)
	public void testCreateRuntimeCommandWithInitializedButNotPresentMockActuatorExpectingExcpetion()
			throws RoboticsException {
		Actuator mockActuator = new InitializedMockActuator();
		Action mockAction = null;
		DeviceParameterBag mockDeviceParameterBag = null;
		new RoboticsContextImpl("dummy").initialize(mockActuator);

		mockRuntime.createRuntimeCommand(mockActuator, mockAction, mockDeviceParameterBag);
	}
}
