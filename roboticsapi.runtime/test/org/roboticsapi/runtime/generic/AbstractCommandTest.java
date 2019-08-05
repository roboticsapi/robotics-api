/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.Command.UnhandledExceptionHandler;
import org.roboticsapi.core.Command.WatchdogTimeoutException;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.EventEffect;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.sensor.Assignment;
import org.roboticsapi.runtime.mockclass.MockCommand;
import org.roboticsapi.runtime.mockclass.MockSensor;

public abstract class AbstractCommandTest extends AbstractRuntimeTest {
	private class MockCommandWithoutInnerExceptions extends MockCommand {
		public MockCommandWithoutInnerExceptions() {
			super("MockCommandWithoutInnerExceptions");
		}

		@Override
		protected Set<CommandRealtimeException> collectInnerExceptions() {
			return null;
		}
	}

	private Command mockSealedCommand;

	@Before
	public void setUpAndSealMockSealedCommand() {
		MockCommand mockCmd = new MockCommand("MockCommand");

		mockCmd.setRuntime(getRuntime());

		mockSealedCommand = mockCmd;

		try {
			mockSealedCommand.seal();
		} catch (RoboticsException e) {
			fail("A RoboticsException occured while sealing the Command object and avoided further Test Cases.");
		}

		assertTrue(mockSealedCommand.isSealed());
	}

	@After
	public void tearDownMockSealedCommand() {
		mockSealedCommand = null;
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000)
	public void testSealWithTestRuntimeAndMockCommand() throws RoboticsException {
		MockCommand mockCmd = new MockCommand();

		mockCmd.setRuntime(getRuntime());

		Command cmd = mockCmd;

		cmd.seal();

		// a further seal()-operation just to test the sealed-branch
		cmd.seal();
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000)
	public void testSealWithTestRuntimeAndMockCommandWithoutInnerExceptions() throws RoboticsException {
		RoboticsRuntime runtime = getRuntime();

		MockCommandWithoutInnerExceptions mockCmd = new MockCommandWithoutInnerExceptions();

		mockCmd.setRuntime(runtime);

		Command cmd = mockCmd;

		assertNotNull(cmd);

		cmd.seal();
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddDoneStateConditionWithMockStateAndMockSealedCommandExpectingException()
			throws RoboticsException {
		mockSealedCommand.addDoneStateCondition(State.True());
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddTakeoverAllowedConditionWithMockStateAndMockSealedCommandExpectingException()
			throws RoboticsException {
		mockSealedCommand.addTakeoverAllowedCondition(State.True());
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddEventHandlerWithMockSealedCommandExpectingException() throws RoboticsException {
		class MockEventEffect extends EventEffect {

		}

		mockSealedCommand.addEventHandler(EventHandler.OnStateEntered(State.True(), new MockEventEffect()));
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testSetWatchdogTimeoutWithMockSealedCommandExpectingException() throws RoboticsException {
		mockSealedCommand.setWatchdogTimeout(1.0);
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000)
	public void testGetInnerExceptionsNeedsWatchdogAndAddWatchdog() {
		Command cmd = new MockCommand();

		assertEquals(0, cmd.getInnerExceptions().size());

		// set watchdogTimeout > 0 in order to needsWatchdog() returns true
		try {
			cmd.setWatchdogTimeout(1);
		} catch (RoboticsException e) {
			fail("Exception at setWatchdogTimeout. Test failed.");
		}

		// one Exception was added
		assertEquals(1, cmd.getInnerExceptions().size());

		// test if the added Exception is a WatchdogTimeoutException
		for (CommandRealtimeException c : cmd.getInnerExceptions()) {
			if (!c.getClass().equals(WatchdogTimeoutException.class)) {
				fail("No WatchdogTimeoutException was added to innerExceptions although watchdog is needed.");
			}
		}
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000)
	public void testAssign() {
		Command mockCmd = new MockCommand();

		Sensor<Double> sensor = new MockSensor<Double>(getRuntime());

		PersistContext<Double> percon = new PersistContext<Double>(mockCmd);

		assertEquals(0, mockCmd.getAssignments().size());

		mockCmd.assign(sensor, percon);

		assertEquals(1, mockCmd.getAssignments().size());

		List<Assignment<?>> assignments = mockCmd.getAssignments();

		assertEquals(mockCmd, assignments.get(0).getTarget().getCommand());
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddExceptionHandlerWithSealedCommandExpectingException() throws RoboticsException {
		mockSealedCommand.addExceptionHandler(null);
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddObserverWithSealedCommandExpectingException() throws RoboticsException {
		mockSealedCommand.addObserver(null);
	}

	// timeout was adopted from other tests
	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddObserverWithMockCommandAndArgumentsForNewObserverExpectingException() throws RoboticsException {
		Command mockCmd = new MockCommand();
		MockSensor<Double> mockSensor = new MockSensor<Double>(getRuntime());
		mockSensor.setAvailable(false);

		// with try-catch because there are 2 cases for the same Exception
		try {
			mockCmd.addObserver(mockSensor, null);
		} catch (RoboticsException e) {
			if ((!mockCmd.isSealed()) && (!mockSensor.isAvailable())) {
				throw e;
			}
		}
	}

	@Test(timeout = 15000)
	public void testAddObserverWithThreeArgumentsForObserverConstructor() throws RoboticsException {
		Command mockCmd = new MockCommand();

		mockCmd.addObserver(new MockSensor<Double>(getRuntime()), null, false);
	}

	@Test(timeout = 15000)
	public void testHandleExceptionWithMockExceptionHandlerExpectingLoggerPrint() {
		UnhandledExceptionHandler mockHandler = new UnhandledExceptionHandler() {
			@Override
			public void handleException(CommandRuntimeException exc) {
				throw new RuntimeException("MockException for Test");
			}
		};

		Command mockCmd = new MockCommand();

		mockCmd.setUnhandledExceptionHandler(mockHandler);

		mockCmd.handleException(null);
	}

	@Test(timeout = 15000, expected = IllegalArgumentException.class)
	public void testAddObserverWithThreeArgumentsWithNullSensorExpectingException() throws RoboticsException {
		new MockCommand().addObserver(null, null, false);
	}
}
