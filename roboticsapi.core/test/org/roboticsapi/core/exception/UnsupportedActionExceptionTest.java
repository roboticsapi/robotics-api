/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.roboticsapi.core.exception.UnsupportedActionException;

public class UnsupportedActionExceptionTest {
	@Test
	public void testConstructorWithStringArgumentSimpleTest() {
		assertNotNull(new UnsupportedActionException("TestUnsupportedActionException"));
	}

	@Test
	public void testConstructorWithStringAndExceptionArgumentsSimpleTest() {
		assertNotNull(new UnsupportedActionException("TestUnsupportedActionException", new Exception()));
	}

	@Test
	public void testConstructorWithExceptionArgumentsSimpleTest() {
		assertNotNull(new UnsupportedActionException(new Exception()));
	}
}
