/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.mockclass.MockWaitCommand;

public class ExceptionThrowerTest {
	@Test
	public void testConstructorWithTwoArgumentsSimpleTest() {
		Command testCommand = new MockWaitCommand(null);

		CommandRealtimeException testException = new CommandRealtimeException("TestCommandRealtimeException");

		ExceptionThrower testThrower = new ExceptionThrower(testCommand, testException);

		assertSame(testException, testThrower.getThrownException());
	}
}
