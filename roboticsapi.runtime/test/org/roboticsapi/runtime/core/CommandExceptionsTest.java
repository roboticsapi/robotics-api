/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.roboticsapi.core.State;
import org.roboticsapi.core.runtime.CommandConfigurationException;
import org.roboticsapi.core.runtime.CommandErrorException;
import org.roboticsapi.core.runtime.CommandJavaExecutorException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.runtime.CommandSensorListenerException;
import org.roboticsapi.core.runtime.CommandStartException;
import org.roboticsapi.runtime.mockclass.MockCommand;

// Tests for classes Command*Exception in package org.roboticsapi.core.runtime:
public class CommandExceptionsTest {
	// Tests for class CommandJavaExecutorException:

	@Test
	public void testConstructorOfCommandJavaExecutorExceptionWithStringArgumentSimpleTest() {
		assertNotNull(new CommandJavaExecutorException("TestCommandJavaExecutorException"));
	}

	@Test
	public void testConstructorOfCommandJavaExecutorExceptionWithStringAndExceptionArgumentsSimpleTest() {
		assertNotNull(new CommandJavaExecutorException("TestCommandJavaExecutorException", new Exception()));
	}

	// Tests for class CommandConfigurationException:

	@Test
	public void testConstructorOfCommandConfigurationExceptionWithStringArgumentSimpleTest() {
		assertNotNull(new CommandConfigurationException("TestCommandConfigurationException"));
	}

	// Tests for class CommandStartException:

	@Test
	public void testConstructorOfCommandStartExceptionExceptionWithStringArgumentSimpleTest() {
		assertNotNull(new CommandStartException("TestCommandStartException"));
	}

	// Tests for class CommandSensorListenerException:

	@Test
	public void testConstructorOfCommandSensorListenerExceptionWithStringArgumentSimpleTest() {
		assertNotNull(new CommandSensorListenerException("TestCommandSensorListenerException"));
	}

	@Test
	public void testConstructorOfCommandSensorListenerExceptionWithStringAndExceptionArgumentsSimpleTest() {
		assertNotNull(new CommandSensorListenerException("TestCommandSensorListenerException", new Exception()));
	}

	// Tests for class CommandErrorException:

	@Test(expected = CommandErrorException.class)
	public void testConstructorOfCommandErrorExceptionWithStringAndCommandArgumentExpectingCommandErrorException()
			throws CommandErrorException {
		CommandErrorException testEx = new CommandErrorException("TestException", new MockCommand());

		assertNotNull(testEx);

		throw testEx;
	}

	@Test(expected = CommandErrorException.class)
	public void testConstructorOfCommandErrorExceptionWithStringAndCommandAndExceptionArgumentExpectingCommandErrorException()
			throws CommandErrorException {
		CommandErrorException testEx = new CommandErrorException("TestException", new MockCommand(), new Exception());

		assertNotNull(testEx);

		throw testEx;
	}

	// Tests for class CommandRealtimeException:

	@Test
	public void testGetExceptionStateSimpleTest() {
		CommandRealtimeException testExc = new CommandRealtimeException("Test CommandRealtimeException");

		State excState = testExc.getExceptionState();
		assertNotNull(excState);

		assertNotNull(State.True());
	}
}
