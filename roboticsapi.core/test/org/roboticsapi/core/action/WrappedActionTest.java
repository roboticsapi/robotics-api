/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.WrappedAction;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.mockclass.MockAction;

public class WrappedActionTest {
	private class MockWrappedAction extends WrappedAction {
		public MockWrappedAction(Action wrappedAction) {
			super(wrappedAction);
		}
	}

	private class MockActionState extends ActionState {
	}

	@Test
	public void testSupportsStateWithAllPossibleLogicalValues() {
		Action testInnerAction = new MockAction(10);
		WrappedAction testWrappedAction;

		ActionState testInnerEventTrueTrue = new MockActionState();
		ActionState testWrappedEventTrueTrue = new MockActionState();

		ActionState testWrappedEventTrueFalse = new MockActionState();

		ActionState testInnerEventFalseTrue = new MockActionState();

		ActionState testInnerEventFalseFalse = new MockActionState();
		ActionState testWrappedEventFalse = new MockActionState();

		// true or true => true:
		testWrappedAction = new MockWrappedAction(testInnerAction);
		testInnerEventTrueTrue.setAction(testInnerAction);
		testWrappedEventTrueTrue.setAction(testWrappedAction);

		assertTrue(testWrappedAction.supportsState(testInnerEventTrueTrue));
		assertTrue(testWrappedAction.supportsState(testWrappedEventTrueTrue));

		// true or false => true:
		testWrappedAction = new MockWrappedAction(testInnerAction);
		testWrappedEventTrueFalse.setAction(testWrappedAction);

		assertTrue(testWrappedAction.supportsState(testWrappedEventTrueFalse));

		// false or true => true:
		testWrappedAction = new MockWrappedAction(testInnerAction);
		testInnerEventFalseTrue.setAction(testInnerAction);

		assertTrue(testWrappedAction.supportsState(testInnerEventFalseTrue));

		// false or false => false:
		testWrappedAction = new MockWrappedAction(testInnerAction);

		assertFalse(testWrappedAction.supportsState(testInnerEventFalseFalse));
		assertFalse(testWrappedAction.supportsState(testWrappedEventFalse));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullArgumentExpectingException() {
		new MockWrappedAction(null);
	}
}
