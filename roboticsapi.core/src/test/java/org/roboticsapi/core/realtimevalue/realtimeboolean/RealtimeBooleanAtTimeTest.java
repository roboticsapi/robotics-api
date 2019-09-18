/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

public class RealtimeBooleanAtTimeTest {

	@Test
	public void testOverrideMethodEqualsWithTwoEqualBooleanAtTimeSensorsExpectingTrue() {
		RealtimeBoolean innerBooleanSensor = new WritableRealtimeBoolean(true);
		RealtimeDouble innerDoubleSensor = RealtimeDouble.createWritable(1);
		final double MAX_AGE = 10;

		RealtimeBooleanAtTime testSensor1 = new RealtimeBooleanAtTime(innerBooleanSensor, innerDoubleSensor, MAX_AGE);
		RealtimeBooleanAtTime testSensor2 = new RealtimeBooleanAtTime(innerBooleanSensor, innerDoubleSensor, MAX_AGE);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoDifferentBooleanAtTimeSensorsExpectingFalse() {
		RealtimeBoolean innerBooleanSensor1 = new WritableRealtimeBoolean(true);
		RealtimeBoolean innerBooleanSensor2 = new WritableRealtimeBoolean(false);

		RealtimeDouble innerDoubleSensor1 = RealtimeDouble.createWritable(1);
		RealtimeDouble innerDoubleSensor2 = RealtimeDouble.createWritable(2);

		RealtimeBooleanAtTime testSensor1 = new RealtimeBooleanAtTime(innerBooleanSensor1, innerDoubleSensor1, 1);
		RealtimeBooleanAtTime testSensor2 = new RealtimeBooleanAtTime(innerBooleanSensor2, innerDoubleSensor2, 2);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoDifferentSensorsExpectingFalse() {
		RealtimeBoolean innerBooleanSensor = new WritableRealtimeBoolean(true);
		RealtimeDouble innerDoubleSensor = RealtimeDouble.createWritable(1);

		RealtimeBooleanAtTime testSensor = new RealtimeBooleanAtTime(innerBooleanSensor, innerDoubleSensor, 10);

		RealtimeInteger testIntegerSensor = RealtimeInteger.createWritable(2);

		assertFalse(testSensor.equals(testIntegerSensor));
	}

	@Test
	public void testOverrideMethodIsAvailableWithBothInnerSensorsAreAvailableExpectingTrue() {
		MockRealtimeBoolean innerBooleanSensor = new MockRealtimeBoolean();
		MockRealtimeDouble innerDoubleSensor = new MockRealtimeDouble(null);

		innerBooleanSensor.setAvailable(true);
		innerDoubleSensor.setAvailable(true);

		RealtimeBooleanAtTime testSensor = new RealtimeBooleanAtTime(innerBooleanSensor, innerDoubleSensor, 10);

		assertTrue(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableWithInnerSensorsAreNotAvailableExpectingTrue() {
		MockRealtimeBoolean innerBooleanSensor = new MockRealtimeBoolean();
		MockRealtimeDouble innerDoubleSensor = new MockRealtimeDouble(null);

		innerBooleanSensor.setAvailable(false);
		innerDoubleSensor.setAvailable(false);

		RealtimeBooleanAtTime testSensor = new RealtimeBooleanAtTime(innerBooleanSensor, innerDoubleSensor, 10);

		assertFalse(testSensor.isAvailable());
	}

	@Test
	public void testOverrideToStringOfSubclassBooleanAtTimeSensorExpectingNotNullSimpleTest() {
		RealtimeBoolean innerBooleanSensor = new WritableRealtimeBoolean(true);
		RealtimeDouble innerDoubleSensor = RealtimeDouble.createWritable(1);

		RealtimeBooleanAtTime testSensor = new RealtimeBooleanAtTime(innerBooleanSensor, innerDoubleSensor, 10);

		assertNotNull(testSensor.toString());
	}
}
