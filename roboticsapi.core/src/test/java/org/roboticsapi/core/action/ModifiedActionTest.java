/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.mockclass.MockAction;

// only unit tests
public class ModifiedActionTest {
	private class MockingModifiedAction<A extends Action> extends ModifiedAction<A> {

		public MockingModifiedAction(A innerAction) {
			super(innerAction);
		}

		public MockingModifiedAction(A innerAction, double watchdogTimeout) {
			super(innerAction, watchdogTimeout);
		}
	}

	private final Action a = new MockAction(10, true, true);

	@Test
	public void testConstructorWithMockingAction() {
		ModifiedAction<Action> ma = new MockingModifiedAction<Action>(a);
		assertNotNull(ma);
		assertEquals(ma.getWatchdogTimeout(), 10, 0.001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithAnotherMockingActionWithInvalidWatchdogTimeoutExpectingException() {
		new MockingModifiedAction<Action>(new MockAction(-10, true, true));
	}

	@Test
	public void testConstructorWithMockingActionAndWatchdogTimeout() {
		ModifiedAction<Action> ma = new MockingModifiedAction<Action>(a, 20);
		assertNotNull(ma);
		assertEquals(ma.getWatchdogTimeout(), 20, 0.001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithMockingActionAndInvalidWatchdogTimeoutExpectingException() {
		new MockingModifiedAction<Action>(a, -20);
	}

	@Test
	public void testGetInnerAction() {
		ModifiedAction<Action> ma = new MockingModifiedAction<Action>(a);
		assertEquals(ma.getInnerAction(), a);
		assertEquals(ma.getInnerAction().getClass(), a.getClass());
	}
}
