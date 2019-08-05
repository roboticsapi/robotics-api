/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.eventhandler.CommandStarter;

public abstract class AbstractCommandStarterTest extends AbstractRuntimeTest {
	private class MockCommandStarter extends CommandStarter {
		private Command contextCommand;

		public MockCommandStarter(Command contextCommand) {
			super(contextCommand);
			setContext(contextCommand);
		}

		@Override
		public void setContext(Command contextCommand) {
			this.contextCommand = contextCommand;
		}

		@Override
		public Command getContext() {
			return contextCommand;
		}

		@Override
		public void validate() {
			super.validate();
		}
	}

	private Command mockCommand = null;
	private MockCommandStarter mockCommandStarter = null;

	@Before
	public void setup() {
		mockCommandStarter = new MockCommandStarter(mockCommand);
	}

	@After
	public void teardown() {
		mockCommand = null;
		mockCommandStarter = null;
	}

	@Test(timeout = 3000)
	public void testValidateWithNullContextSimpleTest() {
		mockCommandStarter.validate();
	}

	@Test(timeout = 3000, expected = IllegalArgumentException.class)
	public void testValidateWithNonTransactionCommandExpectingException() {
		mockCommand = getRuntime().createWaitCommand(1);
		mockCommandStarter.setContext(mockCommand);

		mockCommandStarter.validate();
	}

	@Test(timeout = 3000, expected = IllegalArgumentException.class)
	public void testValidateWithContextEqualsTargetExpectingException() {
		mockCommand = getRuntime().createTransactionCommand();
		mockCommandStarter.setContext(mockCommand);
		mockCommandStarter.setTarget(mockCommand);

		mockCommandStarter.validate();
	}

	@Test(timeout = 3000, expected = IllegalArgumentException.class)
	public void testValidateWithTargetCommandIsNotInTransactionCommandExpectingException() {
		mockCommand = getRuntime().createTransactionCommand();
		mockCommandStarter.setContext(mockCommand);
		mockCommandStarter.setTarget(getRuntime().createWaitCommand(1));

		mockCommandStarter.validate();
	}
}
