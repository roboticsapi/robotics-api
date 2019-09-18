/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.roboticsapi.core.Command.UnhandledExceptionHandler;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.mockclass.MockCommand;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

// only unit tests
public class CommandTest {
	// only the necessary members of State are implemented in MockState
	private class MockState extends RealtimeBoolean {
		@Override
		public boolean isAvailable() {
			return true;
		}
	}

	private final Command mockCmd = new MockCommand(null, null);

	@Test
	public void testValidateStateWithNullStateExpectingFalse() throws RoboticsException {
		assertFalse(mockCmd.validateRealtimeValue(null));
	}

	@Test
	public void testHandleExceptionOfInterfaceUnhandledExceptionHandlerUsingGetUnhandledExceptionHandler() {
		UnhandledExceptionHandler unexha = mockCmd.getUnhandledExceptionHandler();

		unexha.handleException(new CommandRuntimeException("MockCommandRuntimeException"));
	}

	@Test
	public void testGetWatchdogTimeoutAndSetWatchdogTimeout() throws RoboticsException {
		double timeout = mockCmd.getWatchdogTimeout() != 1.0 ? 1.0 : 2.0;

		if (mockCmd.isSealed()) {
			fail("MockCommand is sealed wrongly so the test was canceled.");
		}

		mockCmd.setWatchdogTimeout(timeout);

		assertEquals(mockCmd.getWatchdogTimeout(), timeout, 0.001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWatchdogTimeoutWithNegativeValueExpectingException() {
		try {
			mockCmd.setWatchdogTimeout(-1);
		} catch (RoboticsException e) {
			fail("RoboticsException while setting watchdogTimeout. Test will be aborted.");
		}
	}
}
