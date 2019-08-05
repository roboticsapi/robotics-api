/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.OperationStateUnknownException;
import org.roboticsapi.mockclass.MockAbstractOnlineObject;

public class AbstractOnlineObjectTest {
	private class MockAbstractOnlineObjectWithSpecifiedOperationState extends MockAbstractOnlineObject {
		private OperationState operationState;

		public MockAbstractOnlineObjectWithSpecifiedOperationState(OperationState opState) {
			setState(opState);
		}

		public void setState(OperationState opState) {
			operationState = opState;
		}

		@Override
		public OperationState getState() {
			return operationState;
		}
	}

	private MockAbstractOnlineObject mockAbstractOnlineObject;
	private AbstractOnlineObject mockOnlineObject;

	@Before
	public void setup() {
		mockAbstractOnlineObject = new MockAbstractOnlineObject();
		mockOnlineObject = new MockAbstractOnlineObject();
	}

	@After
	public void teardown() {
		mockAbstractOnlineObject = null;
		mockOnlineObject = null;
	}

	@Test
	public void testCheckPresent() throws OperationStateUnknownException {
		assertTrue(mockAbstractOnlineObject.checkPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMirrorOperationStateWithNullArgumentExpectingException() {
		mockAbstractOnlineObject.mirrorOperationState(null);
	}

	@Test
	public void testMirrorOperationStateWithMockOnlineObject() {
		mockAbstractOnlineObject.mirrorOperationState(mockOnlineObject);

		// TODO: test of the effect of operations above
	}

	@Test
	public void testMirrorOperationStateWithMockOnlineObjectTwice() {
		mockAbstractOnlineObject.mirrorOperationState(mockOnlineObject);
		mockAbstractOnlineObject.mirrorOperationState(mockOnlineObject);

		// TODO: test of the effect of operations above
	}

	@Test
	public void testMirrorOperationStateWithMockOnlineObjectInitialized() throws InitializationException {
		new RoboticsContextImpl("dummy").initialize(mockAbstractOnlineObject);

		mockAbstractOnlineObject.mirrorOperationState(mockOnlineObject);

		// TODO: test of the effect of operations above
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMirrorOperationStateWithItselfExpectingException() {
		mockAbstractOnlineObject.mirrorOperationState(mockAbstractOnlineObject);
	}

	@Test
	public void testMirrorOperationStateWithSeveralSpecifiedOnlineObjects() throws InitializationException {
		// initialize mockAbstractOnlineObject:
		new RoboticsContextImpl("dummy").initialize(mockAbstractOnlineObject);

		MockAbstractOnlineObjectWithSpecifiedOperationState testObject = null;

		testObject = new MockAbstractOnlineObjectWithSpecifiedOperationState(null);

		testObject.setState(OperationState.OFFLINE);

		mockAbstractOnlineObject.mirrorOperationState(testObject);

		testObject.setState(OperationState.SAFEOPERATIONAL);

		mockAbstractOnlineObject.mirrorOperationState(testObject);

		testObject.setState(OperationState.OPERATIONAL);

		mockAbstractOnlineObject.mirrorOperationState(testObject);
	}
}
