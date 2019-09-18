/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.core.actuator.mockclass.MockAbstractOnlineObject;

public class OperationStatesTest {
	// Tests for class OperationStateMachine:

	@Test
	public void testOverrideMethodIsInitializedOfClassOperationStateMachineExpectingFalse() {
		AbstractOnlineObject testAbstractOnlineObject = new MockAbstractOnlineObject();
		OperationStateMachine testOperationStateMachine = new OperationStateMachine(testAbstractOnlineObject);

		assertFalse(testOperationStateMachine.isInitialized());
	}

	// Tests for class AbsentOperationState:

	@Test
	public void testIsInitializedExpectingTrue() {
		AbsentOperationState testState = new AbsentOperationState(null);

		assertTrue(testState.isInitialized());
	}
}
