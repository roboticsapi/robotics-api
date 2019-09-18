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
import org.roboticsapi.core.action.PlannedAction.TimeProgressRealtimeBoolean;

public class PlannedActionTest {
	@Test
	public void testEqualsOfStaticInnerClassTimeProgressState() {
		TimeProgressRealtimeBoolean testTimeProgressState = new TimeProgressRealtimeBoolean(null, null, 1);
		TimeProgressRealtimeBoolean thatTimeProgressStateTrue = new TimeProgressRealtimeBoolean(null, null, 1);
		TimeProgressRealtimeBoolean thatTimeProgressStateFalse = new TimeProgressRealtimeBoolean(null, null, 0);

		assertTrue(testTimeProgressState.equals(thatTimeProgressStateTrue));

		assertFalse(testTimeProgressState.equals(thatTimeProgressStateFalse));

		assertFalse(testTimeProgressState.equals(null));
	}
}
