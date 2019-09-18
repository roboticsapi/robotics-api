/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class GreaterDoubleRealtimeComparatorTest {
	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestValuesExpectingTrue() {
		RealtimeBoolean testSensor = RealtimeDouble.createFromConstant(47.11).greater(-0.42);

		assertTrue(testSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestValuesExpectingFalse() {
		RealtimeBoolean testSensor = RealtimeDouble.createFromConstant(-0.4711).greater(42);

		assertFalse(testSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestSensorsExpectingTrue() {
		RealtimeDouble leftSensor = RealtimeDouble.createWritable(47.11);
		RealtimeDouble rightSensor = RealtimeDouble.createWritable(-0.42);

		RealtimeBoolean testSensor = leftSensor.greater(rightSensor);

		assertTrue(testSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestSensorsExpectingFalse() {
		RealtimeDouble leftSensor = RealtimeDouble.createWritable(-0.4711);
		RealtimeDouble rightSensor = RealtimeDouble.createWritable(42);

		RealtimeBoolean testSensor = leftSensor.greater(rightSensor);

		assertFalse(testSensor.getCheapValue());
	}
}
