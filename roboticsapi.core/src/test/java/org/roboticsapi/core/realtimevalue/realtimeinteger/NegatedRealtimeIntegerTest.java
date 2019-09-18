/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class NegatedRealtimeIntegerTest {
	@Test
	public void testGetInnerSensorOfClassNegatedRealtimeIntegerWithNonnullInnerSensorExpectingReturnedSensorEqualsTestInnerSensor() {
		RealtimeInteger innerSensor = new WritableRealtimeInteger(1);
		NegatedRealtimeInteger testSensor = new NegatedRealtimeInteger(innerSensor);

		assertEquals(innerSensor, testSensor.getInnerValue());
	}

	@Test
	public void testOverrideMethodToStringOfClassNegatedRealtimeIntegerExpectingNotNull() {
		RealtimeInteger innerSensor = new WritableRealtimeInteger(1);
		NegatedRealtimeInteger testSensor = new NegatedRealtimeInteger(innerSensor);

		assertNotNull(testSensor.toString());
	}
}
