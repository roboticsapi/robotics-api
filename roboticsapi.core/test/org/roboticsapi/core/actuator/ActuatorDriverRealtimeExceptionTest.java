/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public class ActuatorDriverRealtimeExceptionTest {
	@Test
	public void testOverrideMethodEqualsWithNullActuatorDriverAndNullArgumentExpectingTrueSimpleTest() {
		ActuatorDriverRealtimeException mockException = new ActuatorDriverRealtimeException(null);

		ActuatorDriverRealtimeException testException = new ActuatorDriverRealtimeException(null);

		assertTrue(mockException.equals(testException));
	}
}
