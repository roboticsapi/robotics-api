/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.roboticsapi.core.AbstractComposedDeviceDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.mockclass.TestActuatorException;
import org.roboticsapi.mockclass.TestRuntime;

// only unit tests
public class AbstractComposedDeviceDriverTest {
	private static class StaticMockComposedDeviceDriver extends AbstractComposedDeviceDriver {
		private static OperationState getAllEqualStateOrUnknownFromMock(OperationState... operationStates) {
			return AbstractComposedDeviceDriver.getAllEqualStateOrUnknownFrom(operationStates);
		}

		private static OperationState getMinimumStateFromMock(OperationState... operationStates) {
			return AbstractComposedDeviceDriver.getMinimumStateFrom(operationStates);
		}
	}

	private class MockComposedDeviceDriver extends AbstractComposedDeviceDriver {
		@Override
		public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
			List<ActuatorDriverRealtimeException> exceptions = new ArrayList<ActuatorDriverRealtimeException>();

			exceptions.add(new ActuatorDriverRealtimeException(null));

			return exceptions;
		}

		public OperationState getMinimumStateFromMock(OperationState... operationStates) {
			return AbstractComposedDeviceDriver.getMinimumStateFrom(operationStates);
		}
	}

	@Test
	public void testGetRuntime() {
		AbstractComposedDeviceDriver mockDriver = new AbstractComposedDeviceDriver() {
		};
		assertNull(mockDriver.getRuntime());

		RoboticsRuntime runtime = new TestRuntime();
		assertNotNull(runtime);

		mockDriver.setRuntime(runtime);
		assertNotNull(mockDriver.getRuntime());
	}

	@Test
	public void testGetAllEqualStateOrUnknownFromWithoutOperationState() {
		StaticMockComposedDeviceDriver.getAllEqualStateOrUnknownFromMock();
	}

	@Test
	public void testGetAllEqualStateOrUnknownFromWithOneOperationState() {
		StaticMockComposedDeviceDriver.getAllEqualStateOrUnknownFromMock(OperationState.UNKNOWN);
	}

	@Test
	public void testGetAllEqualStateOrUnknownFromWithTwoOperationStates() {
		StaticMockComposedDeviceDriver.getAllEqualStateOrUnknownFromMock(OperationState.UNKNOWN, OperationState.NEW);
	}

	@Test
	public void testGetMinimumStateFromWithSeveralOperationStates() {
		OperationState[] states = new OperationState[6];
		states[0] = OperationState.ABSENT;
		states[1] = OperationState.NEW;
		states[2] = OperationState.OFFLINE;
		states[3] = OperationState.OPERATIONAL;
		states[4] = OperationState.SAFEOPERATIONAL;
		states[5] = OperationState.UNKNOWN;

		StaticMockComposedDeviceDriver.getMinimumStateFromMock(states);
	}

	@Test
	public void testIsPresentWithUninitializedMockDriver() {
		AbstractComposedDeviceDriver mockDriver = new AbstractComposedDeviceDriver() {
		};

		assertFalse(mockDriver.isPresent());
	}

	@Test
	public void testDefineActuatorDriverExceptions() {
		AbstractComposedDeviceDriver mockDriver = new AbstractComposedDeviceDriver() {
		};

		assertNotNull(mockDriver.defineActuatorDriverExceptions());
	}

	@Test
	public void testGetExceptionTypesWithMockDriver() {
		AbstractComposedDeviceDriver mockDriver = new MockComposedDeviceDriver();

		assertNotNull(mockDriver.getExceptionTypes());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDefineActuatorDriverExceptionWithNullDriverExceptionExpectingException() {
		try {
			(new AbstractComposedDeviceDriver() {
			}).defineActuatorDriverException(null);
		} catch (CommandException e) {
			fail("Unexpected CommandException. Test will be abortet.");
		}
	}

	@Test
	public void testDefineActuatorDriverExceptionWithMockException() {
		AbstractComposedDeviceDriver mockDriver = new MockComposedDeviceDriver();

		try {
			mockDriver.defineActuatorDriverException(ActuatorDriverRealtimeException.class);
		} catch (CommandException e) {
			fail("Unexpected CommandException. Test will be abortet.");
		}
	}

	@Test(expected = CommandException.class)
	public void testDefineActuatorDriverException() throws CommandException {
		AbstractComposedDeviceDriver mockDriver = new AbstractComposedDeviceDriver() {
			@Override
			public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
				List<ActuatorDriverRealtimeException> exceptions = new ArrayList<ActuatorDriverRealtimeException>();

				exceptions.add(new ActuatorNotOperationalException(this));

				return exceptions;
			}
		};

		mockDriver.defineActuatorDriverException(TestActuatorException.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPriorityWithNullOperationStateExpectingException() {
		MockComposedDeviceDriver mockDriver = new MockComposedDeviceDriver();

		OperationState testNullOpState = null;

		mockDriver.getMinimumStateFromMock(testNullOpState);
	}
}
