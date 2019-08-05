/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Vector;

import org.junit.Test;
import org.roboticsapi.core.AbortCommandStrategy;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.Command.UnhandledExceptionHandler;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.InnerExceptionStrategy;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.DerivedState;
import org.roboticsapi.mockclass.MockCommand;

// only unit tests
public class CommandTest {
	// only the necessary members of State are implemented in MockState
	private class MockState extends State {
	}

	private final Command mockCmd = new MockCommand();

	@Test
	public void testValidateStateWithNullStateExpectingTrue() throws RoboticsException {
		assertTrue(mockCmd.validateState(null));
	}

	@Test
	public void testValidateStateWithDefaultStateValidatorAndDeviredStateExpectingFalse() throws RoboticsException {
		// only the necessary members of Command are implemented in MockCommand
		class MockDerivedState extends DerivedState {
			private final List<State> states = new Vector<State>();

			public MockDerivedState() {
				states.add(new MockState());
			}

			@Override
			public List<State> getStates() {
				return states;
			}
		}

		DerivedState mockDerived = new MockDerivedState();

		assertFalse(mockCmd.validateState(mockDerived));
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

	@Test
	public void testSetDefaultExceptionStrategyWithNullStrategy() {
		Command.setDefaultExceptionStrategy(null);

		assertNull(Command.getDefaultExceptionStrategy());
	}

	@Test
	public void testSetDefaultExceptionStrategyWithMockStrategy() {
		InnerExceptionStrategy strategy = new AbortCommandStrategy();

		Command.setDefaultExceptionStrategy(strategy);

		assertNotNull(Command.getDefaultExceptionStrategy());

		assertSame(Command.getDefaultExceptionStrategy(), strategy);
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
