/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.EventEffect;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.State;
import org.roboticsapi.core.StateValidator;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.mockclass.MockCommand;

public class EventHandlerTest {
	private class MockState extends State {
	}

	private class MockStateValidator implements StateValidator {
		@Override
		public boolean validateState(State event) throws RoboticsException {
			return false;
		}
	}

	private class MockEventEffect extends EventEffect {
	}

	@Test
	public void testValidateWithValidContextCommandButInvalidStateExpectingException() {
		Command mockCmd = new MockCommand();
		State mockState = new MockState();
		StateValidator mockStateValidator = new MockStateValidator();
		EventEffect mockEventEffect = new MockEventEffect();

		mockCmd.addStateValidator(mockStateValidator);

		try {
			assertFalse(mockCmd.validateState(mockState));
		} catch (RoboticsException e) {
			fail("Exception while validating State. Test will be aborted.");
		}

		EventHandler mockEventHandler = null;

		try {
			mockEventHandler = EventHandler.OnStateEntered(mockState, mockEventEffect);
		} catch (RoboticsException e) {
			fail("Exception while creating EventHandler. Test will be aborted.");
		}

		try {
			mockEventHandler.setContext(mockCmd);

			fail("The expected RoboticsException didn't occured. Test failed.");
		} catch (RoboticsException e) {
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetEffectWithNullArgumentExpectingException() throws RoboticsException {
		EventHandler.OnStateEntered(null, null);
	}
}
