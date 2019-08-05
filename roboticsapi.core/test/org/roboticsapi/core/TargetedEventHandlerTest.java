/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.TargetedEventHandler;
import org.roboticsapi.mockclass.MockCommand;

public class TargetedEventHandlerTest {
	private class MockTargetedEventHandler extends TargetedEventHandler {
		public MockTargetedEventHandler(Command context, Command target) {
			super(context, target);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateWithTwoDifferentCommandsExpectingException() {
		new MockTargetedEventHandler(new MockCommand("MockCmd1"), new MockCommand("MockCmd2"));
	}
}
