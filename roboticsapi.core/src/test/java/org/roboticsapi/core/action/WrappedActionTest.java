/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.mockclass.MockAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;

public class WrappedActionTest {
	private class MockWrappedAction extends WrappedAction {
		public MockWrappedAction(Action wrappedAction) {
			super(wrappedAction);
		}
	}

	private class MockActionState extends ActionRealtimeBoolean {
		public MockActionState(Command scope, Action action) {
			super(scope, action);
		}
	}

	@Test
	public void testSupportsStateWithAllPossibleLogicalValues() {
		Action testInnerAction = new MockAction(10, true, true);
		WrappedAction testWrappedAction;

		// true or true => true:
		testWrappedAction = new MockWrappedAction(testInnerAction);
		ActionRealtimeBoolean testInnerEventTrueTrue = new MockActionState(null, testInnerAction);
		ActionRealtimeBoolean testWrappedEventTrueTrue = new MockActionState(null, testWrappedAction);

		assertTrue(testWrappedAction.supportsState(testInnerEventTrueTrue));
		assertTrue(testWrappedAction.supportsState(testWrappedEventTrueTrue));

		// true or false => true:
		testWrappedAction = new MockWrappedAction(testInnerAction);
		ActionRealtimeBoolean testWrappedEventTrueFalse = new MockActionState(null, testWrappedAction);

		assertTrue(testWrappedAction.supportsState(testWrappedEventTrueFalse));

		// false or true => true:
		testWrappedAction = new MockWrappedAction(testInnerAction);
		ActionRealtimeBoolean testInnerEventFalseTrue = new MockActionState(null, testInnerAction);

		assertTrue(testWrappedAction.supportsState(testInnerEventFalseTrue));

		// false or false => false:
		testWrappedAction = new MockWrappedAction(testInnerAction);

		ActionRealtimeBoolean testInnerEventFalseFalse = new MockActionState(null, null);
		ActionRealtimeBoolean testWrappedEventFalse = new MockActionState(null, null);

		assertFalse(testWrappedAction.supportsState(testInnerEventFalseFalse));
		assertFalse(testWrappedAction.supportsState(testWrappedEventFalse));
	}

}
