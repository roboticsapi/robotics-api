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

public class MultipliedRealtimeIntegerTest {
	// Tests for subclass MultipliedRealtimeInteger:

	@Test
	public void testGetMultiplicandExpectingEqualsTestInnerSensor() {
		RealtimeInteger testInnerSensor1 = new WritableRealtimeInteger(1);
		RealtimeInteger testInnerSensor2 = new WritableRealtimeInteger(2);

		MultipliedRealtimeInteger testSensor = new MultipliedRealtimeInteger(testInnerSensor1, testInnerSensor2);

		assertEquals(testInnerSensor1, testSensor.getMultiplicand());
	}

	@Test
	public void testGetMultiplierExpectingEqualsTestInnerSensor() {
		RealtimeInteger testInnerSensor1 = new WritableRealtimeInteger(1);
		RealtimeInteger testInnerSensor2 = new WritableRealtimeInteger(2);

		MultipliedRealtimeInteger testSensor = new MultipliedRealtimeInteger(testInnerSensor1, testInnerSensor2);

		assertEquals(testInnerSensor2, testSensor.getMultiplier());
	}

	@Test
	public void testOverrideMethodToStringExpectingNotNull() {
		RealtimeInteger testInnerSensor1 = new WritableRealtimeInteger(1);
		RealtimeInteger testInnerSensor2 = new WritableRealtimeInteger(2);

		MultipliedRealtimeInteger testSensor = new MultipliedRealtimeInteger(testInnerSensor1, testInnerSensor2);

		assertNotNull(testSensor.toString());
	}
}
