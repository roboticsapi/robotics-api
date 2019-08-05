/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.Command.UnhandledExceptionHandler;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.eventhandler.StateRaiser;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.state.CommandState;
import org.roboticsapi.io.action.SetDigitalValue;
import org.roboticsapi.io.digital.DigitalOutput;
import org.roboticsapi.io.digital.DigitalOutputDriver;
import org.roboticsapi.platform.action.FollowFrame;
import org.roboticsapi.runtime.mockclass.MockCommand;
import org.roboticsapi.runtime.mockclass.MockTransactionCommand;
import org.roboticsapi.runtime.mockclass.TestActuator;
import org.roboticsapi.runtime.mockclass.TestCommandRealtimeException;
import org.roboticsapi.runtime.mockclass.TestWaitCommand;
import org.roboticsapi.world.Frame;

public abstract class AbstractTransactionCommandTest extends AbstractRuntimeTest {
	private class SpecialMockTransactionCommand extends MockTransactionCommand {
		public SpecialMockTransactionCommand(RoboticsRuntime runtime) {
			super(runtime);
		}

		public void relaxWatchdogTimeoutMock(double watchdogTimeout) throws RoboticsException {
			relaxWatchdogTimeoutInternal(watchdogTimeout);
		}

		public void overrideWatchdogTimeoutMock(double watchdogTimeout) throws RoboticsException {
			overrideWatchdogTimeoutInternal(watchdogTimeout);
		}
	}

	private class SealedMockTransactionCommand extends MockTransactionCommand {
		public SealedMockTransactionCommand(RoboticsRuntime runtime) {
			super(runtime);
		}

		@Override
		public boolean isSealed() {
			return true;
		}
	}

	@Test(timeout = 15000)
	public void testStartCommandExecuted() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(0.2);
		long start = System.currentTimeMillis();
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.execute();
		Assert.assertTrue("Transaction(Wait(0.2)) should take at least 200ms",
				System.currentTimeMillis() - start >= 200);
	}

	@Test(timeout = 15000)
	public void testTwoStartCommandsExecuted() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(0.2);
		Command wait2 = getRuntime().createWaitCommand(0.5);
		long start = System.currentTimeMillis();
		TransactionCommand trans = getRuntime().createTransactionCommand(wait, wait2);
		trans.addStartCommand(wait);
		trans.addStartCommand(wait2);
		trans.execute();
		Assert.assertTrue("Transaction(Wait(0.2), Wait(0.5)) should take at least 500ms",
				System.currentTimeMillis() - start >= 500);
	}

	@Test(timeout = 15000)
	public void testTwoCommandsExecuted() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(0.2);
		Command wait2 = getRuntime().createWaitCommand(0.2);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait, wait2);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(wait.getCompletedState(), new CommandStarter(wait2));
		long start = System.currentTimeMillis();
		trans.execute();
		Assert.assertTrue("Transaction(Wait(0.2), Wait(0.2)) should take at least 400ms",
				System.currentTimeMillis() - start >= 400);
	}

	@Test(timeout = 15000)
	public void testTwoTransactionsExecuted() throws RoboticsException {
		called = false;
		Command w1 = getRuntime().createWaitCommand(0.1);
		TransactionCommand t1 = getRuntime().createTransactionCommand(w1);
		t1.addStartCommand(w1);
		Command w2 = getRuntime().createWaitCommand(0.1);
		TransactionCommand t2 = getRuntime().createTransactionCommand(w2);
		t2.addStartCommand(w2);
		TransactionCommand t = getRuntime().createTransactionCommand(t1, t2);
		t.addStartCommand(t1);
		t.addStateEnteredHandler(t1.getCompletedState(), new CommandStarter(t2));
		t2.addStateFirstEnteredHandler(w2.getStartedState(), new JavaExecutor(new Runnable() {
			@Override
			public void run() {
				called = true;
			}
		}));
		t.execute();
		Assert.assertTrue("Two transactions within a transaction should both be executed.", called);
	}

	@Test(timeout = 15000)
	public void testForwardCancel() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(10);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
		long start = System.currentTimeMillis();
		CommandHandle handle = trans.start();
		handle.cancel();
		handle.waitComplete();
		Assert.assertTrue("Transaction(Wait(10), forward cancel) should be cancellable in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testNoAutoForwardCancel() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(1);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		long start = System.currentTimeMillis();
		CommandHandle handle = trans.start();
		handle.cancel();
		handle.waitComplete();
		Assert.assertTrue("Transaction(Wait(1)) should not be cancellable", System.currentTimeMillis() - start >= 1000);
	}

	@Test(timeout = 15000)
	public void testDoubleTransactionExecuted() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(0.2);
		long start = System.currentTimeMillis();
		TransactionCommand trans1 = getRuntime().createTransactionCommand(wait);
		trans1.addStartCommand(wait);
		TransactionCommand trans = getRuntime().createTransactionCommand(trans1);
		trans.addStartCommand(trans1);
		trans.execute();
		Assert.assertTrue("Transaction(Transaction(Wait(0.2))) should take at least 200ms",
				System.currentTimeMillis() - start >= 200);
	}

	@Test(timeout = 15000)
	public void testWaitTransactionWithCancellerInWaitCancellingWait() throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new CommandCanceller());
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandCanceller should cancel Transaction(Wait(10)) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testWaitTransactionWithCancellerInTransactionCancellingWait() throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(bool.isTrue(), new CommandCanceller(wait));
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandCanceller should cancel Transaction(Wait(10)) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testWaitTransactionWaitCancellerInTransactionCancellingTransactionCancellingWait()
			throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(bool.isTrue(), new CommandCanceller());
		trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandCanceller should cancel Transaction(Wait(10)) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testTransactionCommandStopper() throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(bool.isTrue(), new CommandStopper());
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandStopper should stop Transaction(Wait(10)) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testTransactionCommandWaitStopperInTransaction() throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(bool.isTrue(), new CommandStopper(wait));
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandStopper should stop Transaction(Wait(10)) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testTransactionCommandWaitStopperInWait() throws RoboticsException {
		long start = System.currentTimeMillis();
		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new CommandStopper(wait));
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandStopper should stop Transaction(Wait(10)) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	private boolean called = false;
	private CommandException occurredException;

	@Test(timeout = 15000)
	public void testTransactionThreadStarter() throws RoboticsException {
		called = false;
		Command wait = getRuntime().createWaitCommand(10);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
		trans.addStateFirstEnteredHandler(bool.isTrue(), new JavaExecutor(new Runnable() {
			@Override
			public void run() {
				called = true;
			}
		}));
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		Assert.assertEquals(false, called);
		bool.setValue(true);
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		handle.cancel();
		handle.waitComplete();

		// Give the java thread a little time to run
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception thrown");
		}

		Assert.assertEquals(true, called);
	}

	@Test(timeout = 15000)
	public void testTransactionExceptionThrowerDoesNotThrowWhenNotTriggered() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(10);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
		trans.addStateFirstEnteredHandler(bool.isTrue(), new JavaExceptionThrower(new CommandRuntimeException("true")));
		CommandHandle handle = trans.start();
		handle.cancel();
		handle.waitComplete();

	}

	@Test(timeout = 15000)
	public void testTransactionExceptionThrowerThrowsWhenTriggered() throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(10);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
		trans.addStateFirstEnteredHandler(bool.isTrue(), new JavaExceptionThrower(new CommandRuntimeException("true")));

		occurredException = null;
		trans.setUnhandledExceptionHandler(new UnhandledExceptionHandler() {

			@Override
			public void handleException(CommandRuntimeException exc) {
				occurredException = exc;

			}
		});

		CommandHandle handle = trans.start();
		bool.setValue(true);
		Thread.sleep(100);

		handle.cancel();
		handle.waitComplete();
		Assert.assertNotNull(occurredException);
		Assert.assertEquals("true", occurredException.getMessage());
	}

	private class TestState extends CommandState {
	}

	@Test(timeout = 15000)
	public void testTransactionEventRaiser() throws RoboticsException {
		TestState event = new TestState();

		Command wait = getRuntime().createWaitCommand(10);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		trans.addStateFirstEnteredHandler(bool.isTrue(), new StateRaiser(event));
		trans.addStateFirstEnteredHandler(event, new CommandCanceller(wait));

		long start = System.currentTimeMillis();
		CommandHandle handle = trans.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("CommandCanceller should cancel Transaction(Wait(10)) in less than 10s",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testWaitErrorThrowerInWaitDoesNotThrowWhenNotTriggered() throws RoboticsException {
		Command wait = new TestWaitCommand(getRuntime(), 10);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new ExceptionThrower(new TestCommandRealtimeException("Test")));
		CommandHandle handle = trans.start();
		handle.cancel();
		handle.waitComplete();
	}

	@Test(timeout = 15000)
	public void testWaitErrorThrowerInTransactionDoesNotThrowWhenNotTriggered() throws RoboticsException {
		Command wait = new TestWaitCommand(getRuntime(), 10);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(wait);
		trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		trans.addStateFirstEnteredHandler(bool.isTrue(),
				new ExceptionThrower(new TestCommandRealtimeException("Test")));
		CommandHandle handle = trans.start();
		handle.cancel();
		handle.waitComplete();
	}

	@Test(timeout = 15000)
	public void testWaitErrorThrowerInWaitThrowsWhenTriggered() throws RoboticsException {
		TestCommandRealtimeException toThrow = new TestCommandRealtimeException("Test");
		try {
			Command wait = new TestWaitCommand(getRuntime(), 10);
			TransactionCommand trans = getRuntime().createTransactionCommand(wait);
			trans.addStartCommand(wait);
			trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
			BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
			wait.addStateFirstEnteredHandler(bool.isTrue(), new ExceptionThrower(toThrow));
			CommandHandle handle = trans.start();
			bool.setValue(true);
			handle.cancel();
			handle.waitComplete();
			Assert.fail("An exception should have been thrown");
		} catch (CommandRealtimeException ex) {
			Assert.assertEquals(toThrow, ex);
		}
	}

	@Test(timeout = 15000)
	public void testWaitErrorThrowerInTransactionThrowsWhenTriggered() throws RoboticsException {

		// ExtensionRegistry.addExtension(new NetVisualizerExtension());

		TestCommandRealtimeException toThrow = new TestCommandRealtimeException("Test");
		try {
			Command wait = new TestWaitCommand(getRuntime(), 10);
			TransactionCommand trans = getRuntime().createTransactionCommand(wait);
			trans.addStartCommand(wait);
			trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(wait));
			BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
			trans.addStateFirstEnteredHandler(bool.isTrue(), new ExceptionThrower(toThrow));
			CommandHandle handle = trans.start();
			bool.setValue(true);
			handle.cancel();
			handle.waitComplete();
			Assert.fail("An exception should have been thrown");
		} catch (CommandException ex) {
			Assert.assertEquals(toThrow, ex);
		}
	}

	@Test(timeout = 15000)
	public void testSensorEventContext() throws RoboticsException {
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		Command wait1 = getRuntime().createWaitCommand(2);
		Command wait2 = getRuntime().createWaitCommand(2);
		TransactionCommand trans = getRuntime().createTransactionCommand(wait1, wait2);
		trans.addStartCommand(wait1);
		trans.addStateFirstEnteredHandler(wait1.getCompletedState(), new CommandStarter(wait2));
		trans.addStateFirstEnteredHandler(bool.isTrue(), new CommandStopper());
		long start = System.currentTimeMillis();
		CommandHandle handle = trans.start();
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("BooleanSensor without context should use context of event handler",
				System.currentTimeMillis() - start < 2000);
	}

	@Test(timeout = 15000)
	public void testStartCommandNotExecutedWhenPreconditionNotMet() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(5);
		long start = System.currentTimeMillis();
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(BooleanSensor.fromValue(false), wait);
		trans.execute();
		Assert.assertTrue("Command with false precondition should not be executed.",
				System.currentTimeMillis() - start < 3000);
	}

	@Test(timeout = 15000)
	public void testStartCommandExecutedWhenPreconditionMet() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(0.5);
		long start = System.currentTimeMillis();
		TransactionCommand trans = getRuntime().createTransactionCommand(wait);
		trans.addStartCommand(BooleanSensor.fromValue(true), wait);
		trans.execute();
		Assert.assertTrue("Command with true precondition should be executed.",
				System.currentTimeMillis() - start >= 500);
	}

	@Test(timeout = 15000)
	public void testTwoRuntimeCommandsExecutedInTransaction() throws RoboticsException {
		DigitalOutputDriver out = createOutputDriver("testTwoRuntimeCommandsExecutedInTransaction");

		DigitalOutput outPut = new DigitalOutput();
		outPut.setDriver(out);

		getContext().initialize(out);
		getContext().initialize(outPut);

		TransactionCommand trans = getRuntime().createTransactionCommand();

		RuntimeCommand set1 = getRuntime().createRuntimeCommand(outPut, new SetDigitalValue(true),
				outPut.getDefaultParameters());
		trans.addStartCommand(set1);

		RuntimeCommand set2 = getRuntime().createRuntimeCommand(outPut, new SetDigitalValue(false),
				outPut.getDefaultParameters());
		trans.addCommand(set2);
		trans.addStateFirstEnteredHandler(set1.getCompletedState(), new CommandStarter(set2));

		try {
			trans.execute();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception: " + e.getStackTrace());
		}
	}

	protected abstract DigitalOutputDriver createOutputDriver(String name);

	// timeout was adopted from other tests
	@Test(timeout = 15000)
	public void testTwoContraryRuntimeCommandsInTransactionCommandExpectingException() {
		RoboticsRuntime runtime = getRuntime();

		TestActuator mockActuator = new TestActuator("MockActuator", runtime);

		try {
			getContext().initialize(mockActuator);
		} catch (InitializationException e) {
			// e.printStackTrace();
			fail("Exception while initializing MockActuator. Test will be aborted.");
		}

		Frame refFrm = new Frame("ReferenceFrame");

		Frame frm1 = new Frame("FrameUp", refFrm, 0.1, 0.1, 0.1, 0, 0, 0);
		Frame frm2 = new Frame("FrameDown", refFrm, -0.1, -0.1, -0.1, 0, 0, 0);
		FollowFrame ff1 = new FollowFrame(frm1);
		FollowFrame ff2 = new FollowFrame(frm2);

		RuntimeCommand rc1 = null;
		RuntimeCommand rc2 = null;

		try {
			rc1 = runtime.createRuntimeCommand(mockActuator, ff1);
			rc2 = runtime.createRuntimeCommand(mockActuator, ff2);
		} catch (RoboticsException e) {
			fail("Exception while creating RuntimeCommand. Test will be aborted.");
		}

		TransactionCommand tc = null;

		try {
			tc = runtime.createTransactionCommand("TransactionCommand Up-and-down", rc1, rc2);
		} catch (RoboticsException e) {
			fail("Exception while creating TransactionCommand. Test will be aborted.");
		}

		try {
			tc.execute();

			fail("An expected RoboticsException didn't occured.");
		} catch (RoboticsException e) {
		}
	}

	@Test(timeout = 15000)
	public void testConstructorWithNameAndTestRuntimeAndMockCommand() throws RoboticsException {
		assertNotNull(new MockTransactionCommand("MockTransactionCommand", getRuntime(),
				getRuntime().createWaitCommand(0.1)));
	}

	@Test(timeout = 15000)
	public void testConstructorWithTestRuntime() {
		assertNotNull(new MockTransactionCommand(getRuntime()));
	}

	@Test(timeout = 15000)
	public void testConstructorWithTestRuntimeAndMockCommand() throws RoboticsException {
		assertNotNull(new MockTransactionCommand(getRuntime(), getRuntime().createWaitCommand(0.1)) {
		});
	}

	@Test(timeout = 15000)
	public void testAddCommandWithNullRuntime() throws RoboticsException {
		TransactionCommand mockTransCmd = new MockTransactionCommand(null);

		MockCommand mockCmd = new MockCommand();

		assertNull(mockTransCmd.getRuntime());

		mockCmd.setRuntime(getRuntime());

		assertNotNull(mockCmd.getRuntime());

		mockTransCmd.addCommand(mockCmd);

		assertEquals(mockCmd.getRuntime(), mockTransCmd.getRuntime());
	}

	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddCommandWithTestRuntimeAndNullRuntimeForInnerCommandExpectingException()
			throws RoboticsException {
		TransactionCommand mockTransCmd = new MockTransactionCommand(getRuntime());

		Command mockCmd = new MockCommand();

		assertNotNull(mockTransCmd.getRuntime());

		assertNull(mockCmd.getRuntime());

		mockTransCmd.addCommand(mockCmd);
	}

	@Test(timeout = 15000, expected = RoboticsException.class)
	public void testAddStartCommandWithSealedTransactionCommandExpectingException() throws RoboticsException {
		// Preparing and sealing mockTransactionCommand
		TransactionCommand mockTransCmd = new SealedMockTransactionCommand(null);

		mockTransCmd.addStartCommand(null, null);
	}

	@Test(timeout = 15000)
	public void testOverrideWatchdogTimeoutInternal() {
		SpecialMockTransactionCommand mockTransCmd = new SpecialMockTransactionCommand(null);

		TransactionCommand mockInnerCmd = new MockTransactionCommand(null);

		try {
			mockTransCmd.addCommand(mockInnerCmd);
		} catch (RoboticsException e) {
			fail("Exception while adding inner Command. Test will be aborted.");
		}

		try {
			mockTransCmd.overrideWatchdogTimeoutMock(10);
		} catch (RoboticsException e) {
			fail("Exception while running method overrideWatchdogTimeout. Test failed.");
		}
	}

	@Test(timeout = 15000)
	public void testRelaxWatchdogTimeoutInternal() {
		SpecialMockTransactionCommand mockTransCmd = new SpecialMockTransactionCommand(null);

		TransactionCommand mockInnerCmd = new MockTransactionCommand(null);

		try {
			mockTransCmd.addCommand(mockInnerCmd);
		} catch (RoboticsException e) {
			fail("Exception while adding inner Command. Test will be aborted.");
		}

		try {
			mockTransCmd.relaxWatchdogTimeoutMock(10);
		} catch (RoboticsException e) {
			fail("Exception while running method relaxWatchdogTimeout. Test failed.");
		}
	}
}
