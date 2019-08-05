/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.Command.UnhandledExceptionHandler;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.eventhandler.StateRaiser;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.state.CommandState;
import org.roboticsapi.runtime.mockclass.MockWaitCommand;
import org.roboticsapi.runtime.mockclass.TestCommandRealtimeException;
import org.roboticsapi.runtime.mockclass.TestWaitCommand;

public abstract class AbstractWaitCommandTest extends AbstractRuntimeTest {
	private class SpecialMockWaitCommand extends MockWaitCommand {
		public SpecialMockWaitCommand(RoboticsRuntime runtime, double duration) {
			super(runtime, duration);
		}

		private boolean overridden = false;
		private boolean relaxed = false;

		public boolean isOverridden() {
			return overridden;
		}

		public boolean isRelaxed() {
			return relaxed;
		}

		@Override
		public void overrideWatchdogTimeoutInternal(double watchdogTimeout) {
			super.overrideWatchdogTimeoutInternal(watchdogTimeout);

			overridden = true;
		}

		@Override
		public void relaxWatchdogTimeoutInternal(double watchdogTimeout) {
			super.relaxWatchdogTimeoutInternal(watchdogTimeout);

			relaxed = true;
		}
	}

	@Test(timeout = 15000)
	public void testWaitCommandDuration() throws RoboticsException {
		long start = System.currentTimeMillis();
		getRuntime().createWaitCommand(0.2).execute();
		Assert.assertTrue(
				"Wait(0.2) should take at least 190ms, but took only " + (System.currentTimeMillis() - start) + " ms",
				System.currentTimeMillis() - start >= 190);
	}

	@Test(timeout = 15000)
	public void testWaitCancel() throws RoboticsException {
		long start = System.currentTimeMillis();
		CommandHandle handle = getRuntime().createWaitCommand(10).start();
		handle.cancel();
		handle.waitComplete();
		Assert.assertTrue("Wait(10) should be cancellable in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testWaitCommandCanceller() throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new CommandCanceller());
		CommandHandle handle = wait.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandCanceller should cancel Wait(10) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testWaitCommandStopper() throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new CommandStopper());
		CommandHandle handle = wait.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandStopper should stop Wait(10) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	private boolean called = false;
	private CommandException occurredException;

	@Test(timeout = 15000)
	public void testWaitThreadStarter() throws RoboticsException {
		called = false;
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new JavaExecutor(new Runnable() {
			@Override
			public void run() {
				called = true;
			}
		}));
		CommandHandle handle = wait.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		Assert.assertEquals(false, called);
		bool.setValue(true);
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		handle.cancel();
		handle.waitComplete();

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(true, called);
	}

	@Test(timeout = 15000)
	public void testWaitCancelEvent() throws RoboticsException, InterruptedException {
		called = false;
		Command wait = getRuntime().createWaitCommand(10);
		wait.addStateFirstEnteredHandler(wait.getCancelState(),
				new ExceptionThrower(new CommandRealtimeException("Cancelled")));

		CommandHandle handle = wait.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		Assert.assertEquals(false, called);
		handle.cancel();
		try {
			handle.waitComplete();
		} catch (CommandRealtimeException e) {

			Assert.assertEquals("Cancelled", e.getMessage());

		}
	}

	@Test(timeout = 15000)
	public void testWaitSelfCancelEvent() throws RoboticsException {
		called = false;
		Command wait = getRuntime().createWaitCommand(10);
		wait.addStateFirstEnteredHandler(wait.getStartedState(), new CommandCanceller());
		wait.addStateFirstEnteredHandler(wait.getCancelState(),
				new ExceptionThrower(new CommandRealtimeException("Cancelled")));
		try {
			wait.execute();

		} catch (CommandRealtimeException e) {
			Assert.assertEquals("Cancelled", e.getMessage());
		}
	}

	@Test(timeout = 1500)
	public void testWaitExceptionThrowerDoesNotThrowWhenNotTriggered() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new JavaExceptionThrower(new CommandRuntimeException("true")));
		CommandHandle handle = wait.start();
		handle.cancel();
		handle.waitComplete();
	}

	@Test(timeout = 15000)
	public void testWaitJavaExceptionThrowerThrowsWhenTriggered() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new JavaExceptionThrower(new CommandRuntimeException("true")));

		occurredException = null;
		wait.setUnhandledExceptionHandler(new UnhandledExceptionHandler() {

			@Override
			public void handleException(CommandRuntimeException exc) {
				occurredException = exc;
			}
		});

		CommandHandle handle = wait.start();
		bool.setValue(true);
		handle.cancel();
		handle.waitComplete();
		Assert.assertNotNull(occurredException);
		Assert.assertEquals("true", occurredException.getMessage());
	}

	private class TestState extends CommandState {
	}

	@Test(timeout = 15000)
	public void testWaitEventRaiser() throws RoboticsException {
		long start = System.currentTimeMillis();

		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		TestState event = new TestState();
		wait.addStateFirstEnteredHandler(bool.isTrue(), new StateRaiser(event));
		wait.addStateFirstEnteredHandler(event, new CommandCanceller());

		CommandHandle handle = wait.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandCanceller should cancel Wait(10) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testWaitErrorThrowerDoesNotThrowWhenNotTriggered() throws InterruptedException, RoboticsException {
		Command wait = new TestWaitCommand(getRuntime(), 10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		try {
			wait.addStateFirstEnteredHandler(bool.isTrue(),
					new ExceptionThrower(new TestCommandRealtimeException("Test")));
			CommandHandle handle = wait.start();
			handle.cancel();
			handle.waitComplete();
		} catch (CommandRealtimeException e) {
			Assert.fail("An exception should have been thrown");
		}
	}

	@Test(timeout = 15000)
	public void testWaitErrorThrowerThrowsWhenTriggered() throws RoboticsException, InterruptedException {
		TestCommandRealtimeException toThrow = new TestCommandRealtimeException("Test");
		try {
			Command wait = new TestWaitCommand(getRuntime(), 10);
			BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
			wait.addStateFirstEnteredHandler(bool.isTrue(), new ExceptionThrower(toThrow));
			CommandHandle handle = wait.start();
			bool.setValue(true);
			handle.cancel();
			handle.waitComplete();
			Assert.fail("An exception should have been thrown");
		} catch (TestCommandRealtimeException ex) {

			Assert.assertEquals(toThrow, ex);
		}
	}

	@Test(timeout = 15000)
	public void testWaitErrorIgnorerDoesNotThrowWhenNotTriggered() throws RoboticsException, InterruptedException {
		// TODO: As by now, this test rather tests core that runtime
		// (if runtime does not support ErrorRaiser, the test passes as
		// well)
		// We should check that the ErrorRaiser has been executed

		try {
			Command wait = new TestWaitCommand(getRuntime(), 10);
			BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
			wait.addStateFirstEnteredHandler(bool.isTrue(),
					new ExceptionThrower(new TestCommandRealtimeException("Test")));
			wait.addExceptionHandler(TestCommandRealtimeException.class, new ExceptionIgnorer());
			CommandHandle handle = wait.start();
			handle.cancel();
			handle.waitComplete();

		} catch (CommandRealtimeException ex) {

			Assert.fail("No exception expected");
		}

	}

	@Test(timeout = 15000)
	public void testWaitErrorIgnorerDoesNotThrowWhenTriggered() throws RoboticsException, InterruptedException {
		// TODO: As by now, this test rather tests core that runtime
		// (if runtime does not support ErrorRaiser, the test passes as
		// well)
		// We should check that the ErrorRaiser has been executed

		try {
			Command wait = new TestWaitCommand(getRuntime(), 10);
			BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
			wait.addStateFirstEnteredHandler(bool.isTrue(),
					new ExceptionThrower(new TestCommandRealtimeException("Test")));
			wait.addExceptionHandler(TestCommandRealtimeException.class, new ExceptionIgnorer(), true);
			CommandHandle handle = wait.start();
			bool.setValue(true);
			Thread.sleep(100);
			handle.cancel();
			handle.waitComplete();
		} catch (CommandRealtimeException ex) {
			Assert.fail("No exception should have been thrown");
		}
	}

	@Test(timeout = 15000)
	public void testSetDurationWithMockWaitCommand() {
		WaitCommand mockWaitCmd = new MockWaitCommand(getRuntime(), 1.0);

		assertEquals(1.0, mockWaitCmd.getDuration(), 0.001);

		mockWaitCmd.setDuration(2.0);

		assertEquals(2.0, mockWaitCmd.getDuration(), 0.001);
	}

	@Test(timeout = 15000)
	public void testOverrideWatchdogTimeoutInternal() {
		SpecialMockWaitCommand mockWaitCmd = new SpecialMockWaitCommand(getRuntime(), 1.0);

		assertFalse(mockWaitCmd.isOverridden());

		mockWaitCmd.overrideWatchdogTimeoutInternal(2.0);

		assertTrue(mockWaitCmd.isOverridden());
	}

	@Test(timeout = 15000)
	public void testRelaxWatchdogTimeoutInternal() {
		SpecialMockWaitCommand mockWaitCmd = new SpecialMockWaitCommand(getRuntime(), 1.0);

		assertFalse(mockWaitCmd.isRelaxed());

		mockWaitCmd.relaxWatchdogTimeoutInternal(2.0);

		assertTrue(mockWaitCmd.isRelaxed());
	}

	@Test(timeout = 3000, expected = IllegalArgumentException.class)
	public void testSetDurationWithNegativeDurationExpectingIllegalArgumentException() {
		WaitCommand testWaitCommand = new MockWaitCommand(null);
		testWaitCommand.setDuration(-10);
	}
}
