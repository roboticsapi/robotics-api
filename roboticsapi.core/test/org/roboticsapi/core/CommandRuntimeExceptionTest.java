/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.mockclass.MockCommand;

public class CommandRuntimeExceptionTest {
	@Test
	public void testConstructorWithString() {
		CommandRuntimeException e = new CommandRuntimeException("ConstructorWithString");

		assertNotNull(e);

		assertEquals(e.getMessage(), "ConstructorWithString");
	}

	@Test
	public void testConstructorWithStringAndInnerException() {
		CommandRuntimeException inner = new CommandRuntimeException("InnerCommandRuntimeException");

		CommandRuntimeException e = new CommandRuntimeException("ConstructorWithStringAndInnerException", inner);

		assertNotNull(e);

		assertEquals(e.getMessage(), "ConstructorWithStringAndInnerException");
	}

	@Test
	public void testConstructorWithStringAndInnerError() {
		Error err = new Error();

		CommandRuntimeException e = new CommandRuntimeException("ConstructorWithStringAndInnerError", err);

		assertNotNull(e);

		assertEquals(e.getMessage(), "ConstructorWithStringAndInnerError");
	}

	@Test
	public void testConstructorWithStringAndInnerThrowable() {
		Throwable thr = new Throwable();

		CommandRuntimeException e = new CommandRuntimeException("ConstructorWithStringAndInnerThrowable", thr);

		assertNotNull(e);

		assertEquals(e.getMessage(), "ConstructorWithStringAndInnerThrowable");
	}

	@Test
	public void testConstructorWithStringAndCommand() {
		Command cmd = new MockCommand("MockingCommand");

		CommandRuntimeException e = new CommandRuntimeException("ConstructorWithStringAndCommand", cmd);

		assertNotNull(e);

		assertEquals(e.getCommand(), cmd);
	}

	@Test
	public void testConstructorWithStringAndInnerThrowableAndCommand() {
		Command cmd = new MockCommand("MockingCommand");

		Throwable thr = new Throwable();

		CommandRuntimeException e = new CommandRuntimeException("ConstructorWithStringAndInnerThrowableAndCommand", thr,
				cmd);

		assertNotNull(e);

		assertEquals(e.getMessage(), "ConstructorWithStringAndInnerThrowableAndCommand");

		assertEquals(e.getCommand(), cmd);
	}

	@Test
	public void testSetCommandAndTestGetCommand() {
		CommandRuntimeException e = new CommandRuntimeException("TestCommandRuntimeException");

		Command cmd = new MockCommand("MockingCommand");

		assertNull(e.getCommand());

		e.setCommand(cmd);

		assertNotNull(e.getCommand());

		assertEquals(e.getCommand(), cmd);
	}
}
