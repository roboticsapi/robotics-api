/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.mockclass.TestAction;
import org.roboticsapi.mockclass.TestActuator;
import org.roboticsapi.mockclass.TestActuatorDriver;
import org.roboticsapi.mockclass.TestActuatorException;
import org.roboticsapi.mockclass.TestActuatorExceptionSubclass;
import org.roboticsapi.mockclass.TestCommandRealtimeException;
import org.roboticsapi.mockclass.TestRuntime;

public class ExceptionTest {

	private RoboticsContext context;
	private TestRuntime runtime;
	private TestActuator actuator;
	private TestActuatorDriver driver;

	@Before
	public void setup() throws InitializationException {
		context = new RoboticsContextImpl("test");
		runtime = new TestRuntime();
		runtime.setActuatorPresency(true);
		context.initialize(runtime);
		driver = new TestActuatorDriver(runtime);
		context.initialize(driver);
		actuator = new TestActuator("Test", runtime);
		actuator.setDriver(driver);
		context.initialize(actuator);
	}

	@After
	public void teardown() throws RoboticsException {
		context.uninitialize(actuator);
		context.uninitialize(runtime);
		context.destroy();
		context = null;
	}

	@Test(timeout = 200)
	public void testAddingExceptionThrowerAddsErrorToCommandExceptionsAfterSeal() throws RoboticsException {

		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		TestCommandRealtimeException exception = new TestCommandRealtimeException("Test");
		testCmd.addStateFirstEnteredHandler(testCmd.getStartedState(), new ExceptionThrower(exception));

		testCmd.seal();

		Assert.assertTrue(testCmd.getExceptions().contains(exception));
	}

	@Test(timeout = 200)
	public void testAddingExceptionThrowerAddsErrorToInnerCommandExceptions() throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		TestCommandRealtimeException exception = new TestCommandRealtimeException("Test");
		testCmd.addStateFirstEnteredHandler(testCmd.getStartedState(), new ExceptionThrower(exception));

		Assert.assertTrue(testCmd.getInnerExceptions().contains(exception));
	}

	@Test(timeout = 200)
	public void testDefinedActuatorExceptionsContainedInRuntimeCommandExceptionsAfterSeal() throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());
		testCmd.seal();

		Assert.assertFalse(testCmd.getExceptions(TestActuatorException.class).isEmpty());
	}

	@Test(timeout = 200)
	public void testPartiallyHandledActuatorExceptionsContainedInRuntimeCommandExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());
		testCmd.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer());
		testCmd.seal();

		Assert.assertFalse(testCmd.getExceptions(TestActuatorException.class).isEmpty());
	}

	@Test(timeout = 200)
	public void testFullyHandledActuatorExceptionsNotContainedInRuntimeCommandExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());
		testCmd.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), true);
		testCmd.seal();

		Assert.assertTrue(testCmd.getExceptions(TestActuatorException.class).isEmpty());
	}

	@Test(timeout = 200)
	public void testDefinedActuatorExceptionsContainedInRuntimeCommandInnerExceptions() throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());
		Assert.assertNotNull(testCmd.getInnerException(TestActuatorException.class));
	}

	@Test(timeout = 200)
	public void testDefinedActuatorExceptionsContainedInTransactionCommandExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());
		RuntimeCommand testCmd2 = runtime.createRuntimeCommand(actuator, new TestAction());
		TransactionCommand trans = runtime.createTransactionCommand(testCmd, testCmd2);
		trans.seal();

		Assert.assertFalse(trans.getExceptions(TestActuatorException.class).isEmpty());
	}

	@Test(timeout = 200)
	public void testDefinedActuatorExceptionsContainedInTransactionCommandInnerExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());
		RuntimeCommand testCmd2 = runtime.createRuntimeCommand(actuator, new TestAction());
		TransactionCommand trans = runtime.createTransactionCommand(testCmd, testCmd2);

		trans.seal();
		Assert.assertNotNull(trans.getInnerException(TestActuatorException.class));
	}

	@Test(timeout = 200)
	public void testInRuntimeCommandPartiallyHandledActuatorExceptionsContainedInTransactionCommandExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		RuntimeCommand testCmd2 = runtime.createRuntimeCommand(actuator, new TestAction());

		testCmd.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), false);
		testCmd2.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), false);

		TransactionCommand trans = runtime.createTransactionCommand(testCmd, testCmd2);

		trans.seal();

		Assert.assertFalse(trans.getExceptions(TestActuatorException.class).isEmpty());
		Assert.assertNotNull(trans.getInnerException(TestActuatorException.class));
	}

	@Test(timeout = 200)
	public void testInRuntimeCommandFullyHandledActuatorExceptionsNotContainedInTransactionCommandExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		RuntimeCommand testCmd2 = runtime.createRuntimeCommand(actuator, new TestAction());

		testCmd.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), true);
		testCmd2.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), true);

		TransactionCommand trans = runtime.createTransactionCommand(testCmd, testCmd2);

		trans.seal();

		Assert.assertTrue(trans.getExceptions(TestActuatorException.class).isEmpty());

		try {
			trans.getInnerException(TestActuatorException.class);
			Assert.fail();
		} catch (CommandException ex) {
		}
	}

	@Test(timeout = 200)
	public void testActuatorExceptionOnlyHandledInOneRuntimeCommandIsContainedInTransactionCommandExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		RuntimeCommand testCmd2 = runtime.createRuntimeCommand(actuator, new TestAction());

		testCmd.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), true);

		TransactionCommand trans = runtime.createTransactionCommand(testCmd, testCmd2);

		trans.seal();

		Assert.assertFalse(trans.getExceptions(TestActuatorException.class).isEmpty());
		Assert.assertNotNull(trans.getInnerException(TestActuatorException.class));
	}

	@Test(timeout = 200)
	public void testInTransactionCommandPartiallyHandledActuatorExceptionsContainedInTransactionCommandExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		RuntimeCommand testCmd2 = runtime.createRuntimeCommand(actuator, new TestAction());

		TransactionCommand trans = runtime.createTransactionCommand(testCmd, testCmd2);

		trans.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), false);
		trans.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), false);

		trans.seal();

		Assert.assertFalse(trans.getExceptions(TestActuatorException.class).isEmpty());
		Assert.assertNotNull(trans.getInnerException(TestActuatorException.class));
	}

	@Test(timeout = 200)
	public void testInTransactionCommandFullyHandledActuatorExceptionsContainedOnlyInTransactionCommandInnerExceptionsAfterSeal()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());
		RuntimeCommand testCmd2 = runtime.createRuntimeCommand(actuator, new TestAction());
		TransactionCommand trans = runtime.createTransactionCommand(testCmd, testCmd2);

		trans.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), true);
		trans.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), true);

		trans.seal();

		Assert.assertTrue(trans.getExceptions(TestActuatorException.class).isEmpty());
		Assert.assertNotNull(trans.getInnerException(TestActuatorException.class));
	}

	@Test(timeout = 200)
	public void testSuperclassAndSubclassExceptionsInRuntimeCommandErrorsWhenBothDefined() throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		Assert.assertNotNull(testCmd.getInnerException(TestActuatorException.class));
		Assert.assertNotNull(testCmd.getInnerException(TestActuatorExceptionSubclass.class));

		testCmd.seal();

		Assert.assertFalse(testCmd.getExceptions(TestActuatorException.class).isEmpty());
		Assert.assertFalse(testCmd.getExceptions(TestActuatorExceptionSubclass.class).isEmpty());
	}

	@Test(timeout = 200)
	public void testSuperclassExceptionStillInRuntimeCommandErrorsWhenSubclassExceptionHandled()
			throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		testCmd.addExceptionHandler(TestActuatorExceptionSubclass.class, new ExceptionIgnorer(), true);

		Assert.assertNotNull(testCmd.getInnerException(TestActuatorException.class));

		testCmd.seal();

		Assert.assertFalse(testCmd.getExceptions(TestActuatorException.class).isEmpty());
	}

	@Test(timeout = 200)
	public void testSubclassExceptionNotInRuntimeCommandErrorsWhenHandled() throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		testCmd.addExceptionHandler(TestActuatorExceptionSubclass.class, new ExceptionIgnorer(), true);

		testCmd.seal();

		Assert.assertTrue(testCmd.getExceptions(TestActuatorExceptionSubclass.class).isEmpty());
	}

	@Test(timeout = 200)
	public void testSubclassExceptionNotInRuntimeCommandErrorsWhenSuperclassHandled() throws RoboticsException {
		RuntimeCommand testCmd = runtime.createRuntimeCommand(actuator, new TestAction());

		testCmd.addExceptionHandler(TestActuatorException.class, new ExceptionIgnorer(), true);

		testCmd.seal();

		Assert.assertTrue(testCmd.getExceptions(TestActuatorExceptionSubclass.class).isEmpty());
	}
}
