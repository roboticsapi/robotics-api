/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.mockclass.MockAction;

public class ActionRealtimeExceptionTest {
	@Test
	public void testConstructorWithMockAction() {
		assertNotNull(new ActionRealtimeException(new MockAction(10)));
	}

	@Test
	public void testEqualsWithTwoNullActions() {
		ActionRealtimeException are0 = new ActionRealtimeException(null);
		are0.setCommand(null);

		assertNull(are0.getAction());

		assertNotNull(are0.equals(new ActionRealtimeException(null)));
	}

	@Test
	public void testEqualsWithMockActionAndNullAction() {
		ActionRealtimeException are0 = new ActionRealtimeException(new MockAction(10));
		are0.setCommand(null);

		assertNotNull(are0.getAction());

		assertNotNull(are0.equals(new ActionRealtimeException(null)));
	}
}
