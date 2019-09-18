/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.junit.Test;
import org.mockito.Mockito;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.Command.UnhandledExceptionHandler;
import org.roboticsapi.core.CommandRuntimeException;

public class JavaExceptionThrowerTest {

	@Test
	public void testJavaExceptionThrowerForwardsExceptionToUnhandledExceptionHandler() throws InterruptedException {

		CommandRuntimeException test = new CommandRuntimeException("WAAAGH");
		UnhandledExceptionHandler handler = Mockito.mock(UnhandledExceptionHandler.class);
		Command command = Mockito.mock(Command.class);
		Mockito.when(command.getUnhandledExceptionHandler()).thenReturn(handler);

		test.setCommand(command);
		JavaExceptionThrower alpha = new JavaExceptionThrower(test);
		alpha.getRunnable().run();
		Mockito.verify(handler).handleException(test);

	}

	@Test
	public void TestWithNoArgument() {

		CommandRuntimeException test = new CommandRuntimeException(null);
		JavaExceptionThrower alpha = new JavaExceptionThrower(test);

	}

}
