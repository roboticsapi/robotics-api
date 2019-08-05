/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.runtime.ActionSensorException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.mockclass.MockAction;

public class ActionTest {
	private Action a, b;

	@Before
	public void setUp() {
		a = new MockAction(0); // watchdogTimeout = 0 (INFINITY)
		b = new MockAction(1); // watchdogTimeout positive
	}

	@After
	public void tearDown() {
		a = null;
		b = null;
	}

	@Test
	public void testGetWatchdogTimeoutWithActionWithZeroTimeout() {
		assertEquals(0.0, a.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testGetWatchdogTimeoutWithActionWithPositiveTimeout() {
		assertEquals(1.0, b.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testSetWatchdogTimeoutZeroValueWithActionWithZeroTimeout() {
		a.setWatchdogTimeout(0);

		assertEquals(0.0, a.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testSetWatchdogTimeoutZeroValueWithActionWithPositiveTimeout() {
		b.setWatchdogTimeout(0);

		assertEquals(0.0, b.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testSetWatchdogTimeoutPositiveValueWithActionWithZeroTimeout() {
		a.setWatchdogTimeout(2);

		assertEquals(2.0, a.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testSetWatchdogTimeoutPositiveValueWithActionWithPositiveTimeout() {
		b.setWatchdogTimeout(2);

		assertEquals(2.0, b.getWatchdogTimeout(), 0.001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWatchdogTimeoutNegativeValueWithActionWithZeroTimeoutExpectingException() {
		a.setWatchdogTimeout(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWatchdogTimeoutNegativeValueWithActionWithPositiveTimeoutExpectingException() {
		b.setWatchdogTimeout(-1);
	}

	@Test
	public void testConstructorWithZeroWatchdogTimeoutOnNotNull() {
		assertNotNull(new MockAction(0));
	}

	@Test
	public void testConstructorWithPositiveWatchdogTimeoutOnNotNull() {
		assertNotNull(new MockAction(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNegativeWatchdogTimeoutExpectingException() {
		new MockAction(-1);
	}

	@Test
	public void testRelaxWatchdogTimeoutWithActionWithZeroTimeoutExpectingNoUpdate() {
		a.setWatchdogTimeout(0);

		a.relaxWatchdogTimeout(2);

		assertEquals(0.0, a.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testRelaxWatchdogTimeoutLowerTimeoutWithActionWithPositiveTimeoutExpectingNoUpdate() {
		b.setWatchdogTimeout(2);

		b.relaxWatchdogTimeout(1);

		assertEquals(2.0, b.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testRelaxWatchdogTimeoutHigherTimeoutWithActionWithPositiveTimeout() {
		b.setWatchdogTimeout(2);

		b.relaxWatchdogTimeout(3);

		assertEquals(3.0, b.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testRelaxWatchdogTimeoutNegativeValueWithActionWithZeroTimeoutExpectingNoUpdate() {
		a.setWatchdogTimeout(0);

		a.relaxWatchdogTimeout(-10);

		assertEquals(0.0, a.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testRelaxWatchdogTimeoutNegativeValueWithActionWithPositiveTimeoutExpectingNoUpdate() {
		a.setWatchdogTimeout(1);

		a.relaxWatchdogTimeout(-10);

		assertEquals(1.0, a.getWatchdogTimeout(), 0.001);
	}

	@Test
	public void testActiveOnNotNull() {
		assertNotNull(Action.active());
	}

	@Test
	public void testGetActiveStateOnEquality() {
		ActionState active = Action.active();

		active.setAction(a);

		assertEquals(active, a.getActiveState());
	}

	@Test
	public void testDefineActionExceptionsCheckActionCancelledException() throws CommandException {
		a.defineActionExceptions(ActionCancelledException.class);
	}

	@Test(expected = CommandException.class)
	public void testDefineActionExceptionsWithDefaultExceptionCheckActionSensorExceptionExpectingException()
			throws CommandException {
		a.defineActionExceptions(ActionSensorException.class);
	}

	@Test
	public void testGetExceptionTypesCheckEveryElementInTheList() {
		List<ActionRealtimeException> exceptions = a.defineActionExceptions();
		List<Class<? extends ActionRealtimeException>> types = a.getExceptionTypes();

		int n = exceptions.size() == types.size() ? exceptions.size() : -1;

		if (n < 0) {
			fail("Exception types of getExceptionTypes() don't match with the exception types of defineActionExceptions()!");
		}

		for (int i = 0; i < n; i++) {
			assertEquals(exceptions.get(i).getClass(), types.get(i));
		}
	}
}
