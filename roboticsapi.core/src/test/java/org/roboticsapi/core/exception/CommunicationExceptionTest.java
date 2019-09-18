/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CommunicationExceptionTest {
	@Test
	public void testConstructorWithStringArgumentSimpleTest() {
		assertNotNull(new CommunicationException("TestCommunicationException"));
	}

	@Test
	public void testConstructorWithStringAndExceptionArgumentsSimpleTest() {
		assertNotNull(new CommunicationException("TestCommunicationException", new Exception()));
	}

	@Test
	public void testConstructorWithExceptionArgumentsSimpleTest() {
		assertNotNull(new CommunicationException(new Exception()));
	}
}
