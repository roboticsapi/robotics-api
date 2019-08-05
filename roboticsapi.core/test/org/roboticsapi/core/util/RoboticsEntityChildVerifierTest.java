/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RoboticsEntityChildVerifier;
import org.roboticsapi.mockclass.MockEntityImpl;
import org.roboticsapi.mockclass.MockRoboticsObjectImpl;

public class RoboticsEntityChildVerifierTest {
	private RoboticsObject mockObject = null;
	private RoboticsEntityChildVerifier mockVerifier = null;
	private RoboticsContext ctx = null;

	@Before
	public void setup() throws RoboticsException {
		mockObject = new MockRoboticsObjectImpl();
		mockVerifier = new RoboticsEntityChildVerifier(mockObject);
		ctx = new RoboticsContextImpl("");
	}

	@After
	public void teardown() {
		mockVerifier = null;
		mockObject = null;
		ctx.destroy();
	}

	@Test
	public void testAcceptAddingSimpleTest() throws EntityException {
		mockVerifier.acceptAdding(new MockEntityImpl());
	}

	@Test(expected = EntityException.class)
	public void testAcceptAddingExpectingException() throws EntityException, InitializationException {
		ctx.initialize(mockObject);

		mockVerifier.acceptAdding(new MockEntityImpl());
	}

	@Test
	public void testAcceptRemovingSimpleTest() throws EntityException {
		mockVerifier.acceptRemoving(new MockEntityImpl());
	}

	@Test(expected = EntityException.class)
	public void testAcceptRemovingExpectingException() throws EntityException, InitializationException {
		ctx.initialize(mockObject);

		mockVerifier.acceptRemoving(new MockEntityImpl());
	}
}
