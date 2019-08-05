/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.runtime.mockclass.TestCommandRealtimeException;

public abstract class AbstractExceptionHandlerTest extends AbstractRuntimeTest {

	Semaphore sem;
	boolean flag;
	boolean flag2;

	@Before
	public void setup() {
		sem = new Semaphore(1);
		flag = false;
		flag2 = false;
	}

	@Test(timeout = 2000)
	public void testExceptionThrowerFiresException() throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		BooleanFromJavaSensor activator = new BooleanFromJavaSensor(false);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(activator.isTrue(), new ExceptionThrower(thrownException));

		wait.addStateEnteredHandler(State.fromException(thrownException), new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				sem.release();

			}
		}, true));

		sem.acquire();

		CommandHandle handle = wait.start();

		activator.setValue(true);

		sem.acquire();

		try {
			handle.waitComplete();

			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
		}
	}

	@Test(timeout = 2000)
	public void testRealtimeThrownExceptionIsThrownInJava() throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		try {
			wait.execute();

			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
			Assert.assertEquals(thrownException, e);
		}
	}

	@Test(timeout = 2000)
	public void testRealtimeThrownExceptionAbortsCommandWhenOnlyPartiallyHandled()
			throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		wait.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer());

		wait.addStateEnteredHandler(wait.getActiveState().forSeconds(0.5), new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				flag = true;
			}
		}));

		try {
			wait.execute();

			Assert.assertFalse("WaitCommand should not have been active for 0.5s", flag);
		} catch (CommandRealtimeException e) {
		}
	}

	@Test(timeout = 2000)
	public void testRealtimeThrownExceptionIsThrownInJavaWhenOnlyPartiallyHandled()
			throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		wait.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer());

		try {
			wait.execute();

			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
			Assert.assertEquals(thrownException, e);
		}
	}

	@Test(timeout = 2000)
	public void testRealtimeThrownExceptionDoesNotAbortCommandWhenFullyHandled()
			throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		wait.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer(), true);

		wait.addStateEnteredHandler(wait.getActiveState().forSeconds(0.5), new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				flag = true;
			}
		}));

		try {
			wait.execute();

			Assert.assertTrue("WaitCommand should have been active for >0.5s", flag);
		} catch (CommandRealtimeException e) {
			Assert.fail("Exception should not have been thrown");
		}
	}

	@Test(timeout = 2000)
	public void testRealtimeThrownExceptionIsNotThrownInJavaWhenFullyHandled()
			throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		wait.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer(), true);

		try {
			wait.execute();
		} catch (CommandRealtimeException e) {
			Assert.fail("Exception should not have been thrown");
		}
	}

	@Test(timeout = 2000)
	public void testExceptionPropagated() throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		TransactionCommand trans = getRuntime().createTransactionCommand();

		trans.addStartCommand(wait);

		trans.addExceptionHandler(CommandRealtimeException.class, new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				flag = true;

			}
		}));

		try {
			trans.execute();

			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
			Assert.assertEquals(thrownException, e);
			Assert.assertTrue("Exception should have been propagated to TransactionCommand", flag);
		}
	}

	@Test(timeout = 2000)
	public void testExceptionPropagatedWhenOnlyPartiallyHandled() throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		wait.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer());

		TransactionCommand trans = getRuntime().createTransactionCommand();
		trans.addStartCommand(wait);

		trans.addExceptionHandler(CommandRealtimeException.class, new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				flag = true;
			}
		}));

		try {
			trans.execute();

			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
			Assert.assertEquals(thrownException, e);
			Assert.assertTrue("Exception should have been propagated to TransactionCommand", flag);
		}
	}

	@Test(timeout = 2000)
	public void testExceptionNotPropagatedWhenFullyHandled() throws RoboticsException, InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);

		CommandRealtimeException thrownException = new CommandRealtimeException("Exception");
		wait.addStateEnteredHandler(wait.getStartedState(), new ExceptionThrower(thrownException));

		wait.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer(), true);

		TransactionCommand trans = getRuntime().createTransactionCommand();
		trans.addStartCommand(wait);

		trans.addExceptionHandler(CommandRealtimeException.class, new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				flag = true;
			}
		}));

		try {
			trans.execute();
			Assert.assertFalse("Exception should not have been propagated to TransactionCommand", flag);

		} catch (CommandRealtimeException e) {
			Assert.fail("Exception should not have been thrown");
		}
	}

	@Test(timeout = 2000)
	public void testSuperclassExceptionHandlerHandlesSubclassException() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(1);

		wait.addStateFirstEnteredHandler(wait.getStartedState(),
				new ExceptionThrower(new CommandRealtimeException("Exception subclass") {

					/**
							 *
							 */
					private static final long serialVersionUID = 1L;
				}));

		wait.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer(), true);

		try {
			wait.execute();
		} catch (CommandRealtimeException e) {
			Assert.fail("Exception should not have been thrown");
		}
	}

	@Test(timeout = 2000)
	public void testSubclassExceptionHandlerDoesNotHandleSuperclassException() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(1);

		wait.addStateFirstEnteredHandler(wait.getStartedState(),
				new ExceptionThrower(new CommandRealtimeException("Exception subclass")));

		wait.addExceptionHandler(TestCommandRealtimeException.class, new ExceptionIgnorer(), true);

		try {
			wait.execute();

			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
		}
	}

	@Test(timeout = 2000)
	public void testSuperclassExceptionHandlerExecutedThoughSubclassExceptionHandlerPresent() throws RoboticsException {
		Command wait = getRuntime().createWaitCommand(1);

		wait.addStateFirstEnteredHandler(wait.getStartedState(),
				new ExceptionThrower(new TestCommandRealtimeException("Exception subclass")));

		wait.addExceptionHandler(CommandRealtimeException.class, new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				flag = true;

			}
		}), false);

		wait.addExceptionHandler(TestCommandRealtimeException.class, new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				flag2 = true;

			}
		}), false);

		try {
			wait.execute();

			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
			Assert.assertTrue("Superclass Exception handler should have been executed", flag);

			Assert.assertTrue("Subclass Exception handler should have been executed", flag2);
		}
	}

	@Test(timeout = 5000)
	public void testThrowingSameExceptionTypeTwiceInTransactionDoesNotMergeExceptions() throws RoboticsException {

		Command wait1 = getRuntime().createWaitCommand(0.1);

		Command wait2 = getRuntime().createWaitCommand(0.1);

		TransactionCommand trans = getRuntime().createTransactionCommand();

		trans.addStartCommand(wait1);
		trans.addCommand(wait2);

		trans.addStateEnteredHandler(wait1.getCompletedState(), new CommandStarter(wait2));

		wait1.addStateFirstEnteredHandler(State.False(),
				new ExceptionThrower(new CommandRealtimeException("not to be thrown")));

		wait2.addStateFirstEnteredHandler(State.True(),
				new ExceptionThrower(new CommandRealtimeException("to be thrown")));

		try {
			trans.execute();
		} catch (CommandRealtimeException e) {
			if ("not to be thrown".equals(e.getMessage())) {
				Assert.fail("This exception should not have been thrown");
			}
		}

	}
}
