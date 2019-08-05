/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.Test;
import org.roboticsapi.core.AbortCommandStrategy;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.mockclass.MockCommand;

// this Test Case only checks if the method AbortCommandStrategy#handleException can called correctly
public class AbortCommandStrategyTest {
	private final AbortCommandStrategy acs = new AbortCommandStrategy();
	private final Command command = new MockCommand("MockingCommand");

	@Test(expected = NullPointerException.class)
	public void testHandleExceptionWithNullCommandExpectingNullPointerException() throws RoboticsException {
		acs.handleException(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testHandleExceptionWithMockingCommandAndNullExceptionExpectingNullPointerException()
			throws RoboticsException {
		acs.handleException(command, null);
	}

	@Test
	public void testHandleExceptionWithMockingCommand() throws RoboticsException {
		acs.handleException(command, new CommandRealtimeException("MockingCommandRealtimeException", command));
	}
}
