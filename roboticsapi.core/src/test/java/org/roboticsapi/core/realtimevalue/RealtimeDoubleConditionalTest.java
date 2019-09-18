/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class RealtimeDoubleConditionalTest {
	@Test
	public void testConstructorWithBooleanSensorDoubleSensorAndDoubleValueExpectingNotNullSimpleTest() {
		RealtimeBoolean testBooleanConditionSensor = RealtimeBoolean.createWritable(false);
		RealtimeDouble testDoubleSensorIfTrue = RealtimeDouble.createWritable(1.5);

		assertNotNull(RealtimeDouble.createConditional(testBooleanConditionSensor, testDoubleSensorIfTrue,
				RealtimeDouble.createFromConstant(-0.3)));
	}

	@Test
	public void testConstructorWithBooleanSensorDoubleValueAndDoubleSensorExpectingNotNullSimpleTest() {
		RealtimeBoolean testBooleanConditionSensor = RealtimeBoolean.createWritable(false);
		RealtimeDouble testDoubleSensorIfFalse = RealtimeDouble.createWritable(-0.3);

		assertNotNull(RealtimeDouble.createConditional(testBooleanConditionSensor, 1.5, testDoubleSensorIfFalse));
	}

	@Test
	public void testConstructorWithBooleanSensorAndTwoDoubleValuesExpectingNotNullSimpleTest() {
		RealtimeBoolean testBooleanConditionSensor = RealtimeBoolean.createWritable(false);

		assertNotNull(RealtimeDouble.createConditional(testBooleanConditionSensor, 1.5, -0.3));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoEqualSensorsExpectingTrue() {
		RealtimeBoolean testBooleanConditionSensor = RealtimeBoolean.createWritable(false);
		RealtimeDouble testDoubleSensorIfTrue = RealtimeDouble.createWritable(1.5);
		RealtimeDouble testDoubleSensorIfFalse = RealtimeDouble.createWritable(-0.3);

		RealtimeDouble testDoubleConditionalSensor1 = RealtimeDouble.createConditional(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		RealtimeDouble testDoubleConditionalSensor2 = RealtimeDouble.createConditional(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertTrue(testDoubleConditionalSensor1.equals(testDoubleConditionalSensor2));

		assertTrue(testDoubleConditionalSensor2.equals(testDoubleConditionalSensor1));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoDifferentSensorsExpectingFalse() {
		RealtimeBoolean testBooleanConditionSensor1 = RealtimeBoolean.createWritable(false);
		RealtimeBoolean testBooleanConditionSensor2 = RealtimeBoolean.createWritable(true);

		RealtimeDouble testDoubleSensorIfTrue = RealtimeDouble.createWritable(1.5);
		RealtimeDouble testDoubleSensorIfFalse = RealtimeDouble.createWritable(-0.3);

		RealtimeDouble testDoubleConditionalSensor1 = RealtimeDouble.createConditional(testBooleanConditionSensor1,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		RealtimeDouble testDoubleConditionalSensor2 = RealtimeDouble.createConditional(testBooleanConditionSensor2,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertFalse(testDoubleConditionalSensor1.equals(testDoubleConditionalSensor2));

		assertFalse(testDoubleConditionalSensor2.equals(testDoubleConditionalSensor1));
	}

	@Test
	public void testOverrideMethodIsAvailableWithAllThreeInnerSensorsAreAvailableExpectingTrue() {
		RealtimeBoolean testBooleanConditionSensor = RealtimeBoolean.createWritable(false);
		RealtimeDouble testDoubleSensorIfTrue = RealtimeDouble.createWritable(1.5);
		RealtimeDouble testDoubleSensorIfFalse = RealtimeDouble.createWritable(-0.3);

		assertTrue(testBooleanConditionSensor.isAvailable());
		assertTrue(testDoubleSensorIfTrue.isAvailable());
		assertTrue(testDoubleSensorIfFalse.isAvailable());

		RealtimeDouble testDoubleConditionalSensor = RealtimeDouble.createConditional(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertTrue(testDoubleConditionalSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableWithOneInnerSensorIsNotAvailableExpectingFalse() {
		MockRealtimeBoolean testBooleanConditionSensor = new MockRealtimeBoolean();
		testBooleanConditionSensor.setAvailable(false);

		assertFalse(testBooleanConditionSensor.isAvailable());

		RealtimeDouble testDoubleSensorIfTrue = RealtimeDouble.createWritable(1.5);
		RealtimeDouble testDoubleSensorIfFalse = RealtimeDouble.createWritable(-0.3);

		RealtimeDouble testDoubleConditionalSensor = RealtimeDouble.createConditional(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertFalse(testDoubleConditionalSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodToStringExpectingNotNullSimpleTest() {
		RealtimeBoolean testBooleanConditionSensor = RealtimeBoolean.createWritable(false);
		RealtimeDouble testDoubleSensorIfTrue = RealtimeDouble.createWritable(1.5);
		RealtimeDouble testDoubleSensorIfFalse = RealtimeDouble.createWritable(-0.3);

		RealtimeDouble testDoubleConditionalSensor = RealtimeDouble.createConditional(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertNotNull(testDoubleConditionalSensor.toString());
	}
}