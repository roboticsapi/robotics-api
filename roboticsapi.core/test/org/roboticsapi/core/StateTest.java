/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.State;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.state.ActuatorState;
import org.roboticsapi.mockclass.MockAction;
import org.roboticsapi.mockclass.MockActuatorImpl;

// Tests for class State and its subclasses:
public class StateTest {
	// Tests for class State:

	private class MockState extends State {
	}

	@Test
	public void testSubclassEqualsWithEqualClass() {
		State mockState1 = new MockState();
		State mockState2 = new MockState();

		assertFalse(mockState1.subclassEquals(mockState2));
	}

	@Test
	public void testSubclassEqualsWithDifferentClasses() {
		class SubMockState extends MockState {
		}

		State mockState = new MockState();
		State subMockState = new SubMockState();

		assertTrue(mockState.subclassEquals(subMockState));
	}

	@Test
	public void testFromExceptions() {
		List<CommandRealtimeException> exceptions = new ArrayList<CommandRealtimeException>();

		exceptions.add(new CommandRealtimeException("MockCommandRealtimeException"));

		assertNotNull(State.fromExceptions(exceptions));
	}

	// Tests for subclass ActionState:

	private class MockActionState extends ActionState {
	}

	private class SubMockActionState extends MockActionState {
	}

	@Test
	public void testOverrideMethodSubclassEqualsOfSubclassActionStateIsTrue() {
		Action testAction = new MockAction(1); // watchdogTimeout = 1
		ActionState testActionState = new MockActionState();
		SubMockActionState testSubActionState = new SubMockActionState();

		testActionState.setAction(testAction);
		testSubActionState.setAction(testAction);

		assertTrue(testActionState.subclassEquals(testSubActionState));
	}

	@Test
	public void testOverrideMethodSubclassEqualsOfSubclassActionStateIsFalse() {
		Action testAction1 = new MockAction(1); // watchdogTimeout = 1
		Action testAction2 = new MockAction(2); // watchdogTimeout = 1

		ActionState testActionState = new MockActionState();
		SubMockActionState testSubActionState = new SubMockActionState();

		testActionState.setAction(testAction1);
		testSubActionState.setAction(testAction2);

		assertFalse(testActionState.subclassEquals(testSubActionState));
	}

	// Tests for subclass ActuatorState:

	private class MockActuatorState extends ActuatorState {
	}

	private class SubMockActuatorState extends MockActuatorState {
	}

	@Test
	public void testOverrideMethodSubclassEqualsOfSubclassActuatorStateIsTrue() {
		Actuator testActuator = new MockActuatorImpl();
		ActuatorState testActuatorState = new MockActuatorState();
		SubMockActuatorState testSubActuatorState = new SubMockActuatorState();

		testActuatorState.setDevice(testActuator);
		testSubActuatorState.setDevice(testActuator);

		assertTrue(testActuatorState.subclassEquals(testSubActuatorState));
	}

	@Test
	public void testOverrideMethodSubclassEqualsOfSubclassActuatorStateIsFalse() {
		Actuator testActuator1 = new MockActuatorImpl();
		Actuator testActuator2 = new MockActuatorImpl();

		ActuatorState testActuatorState = new MockActuatorState();
		SubMockActuatorState testSubActuatorState = new SubMockActuatorState();

		testActuatorState.setDevice(testActuator1);
		testSubActuatorState.setDevice(testActuator2);

		assertFalse(testActuatorState.subclassEquals(testSubActuatorState));
	}
}
