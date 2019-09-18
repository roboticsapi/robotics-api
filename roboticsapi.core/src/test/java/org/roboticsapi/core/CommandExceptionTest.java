/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CommandExceptionTest {
	@Test
	public void testConstructorWithString() {
		CommandException e = new CommandException("ConstructorWithString");

		assertNotNull(e);

		assertEquals("ConstructorWithString", e.getMessage());
	}

	@Test
	public void testConstructorWithStringAndInnerException() {
		CommandException inner = new CommandException("InnerCommandException");

		CommandException e = new CommandException("ConstructorWithStringAndInnerException", inner);

		assertNotNull(e);

		assertEquals("ConstructorWithStringAndInnerException", e.getMessage());
	}

	@Test
	public void testConstructorWithStringAndInnerError() {
		Error err = new Error();

		CommandException e = new CommandException("ConstructorWithStringAndInnerError", err);

		assertNotNull(e);

		assertEquals("ConstructorWithStringAndInnerError", e.getMessage());
	}

	@Test
	public void testConstructorWithStringAndInnerThrowable() {
		Throwable thr = new Throwable();

		CommandException e = new CommandException("ConstructorWithStringAndInnerThrowable", thr);

		assertNotNull(e);

		assertEquals("ConstructorWithStringAndInnerThrowable", e.getMessage());
	}
}
