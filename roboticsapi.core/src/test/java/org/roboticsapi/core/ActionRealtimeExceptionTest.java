/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.roboticsapi.core.mockclass.MockAction;

public class ActionRealtimeExceptionTest {
	@Test
	public void testConstructorWithMockAction() {
		assertNotNull(new ActionRealtimeException(new MockAction(10, true, true)));
	}

	@Test
	public void testEqualsWithTwoNullActions() {
		ActionRealtimeException are0 = new ActionRealtimeException(null, null);
		are0.setCommand(null);

		assertNull(are0.getAction());

		assertNotNull(are0.equals(new ActionRealtimeException(null, null)));
	}

	@Test
	public void testEqualsWithMockActionAndNullAction() {
		ActionRealtimeException are0 = new ActionRealtimeException(new MockAction(10, true, true));
		are0.setCommand(null);

		assertNotNull(are0.getAction());

		assertNotNull(are0.equals(new ActionRealtimeException(null, null)));
	}
}
