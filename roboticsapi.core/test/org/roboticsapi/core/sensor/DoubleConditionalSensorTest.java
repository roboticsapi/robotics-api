/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleConditionalSensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.mockclass.MockSensor;

public class DoubleConditionalSensorTest {
	@Test
	public void testConstructorWithBooleanSensorDoubleSensorAndDoubleValueExpectingNotNullSimpleTest() {
		Sensor<Boolean> testBooleanConditionSensor = new BooleanFromJavaSensor(false);
		Sensor<Double> testDoubleSensorIfTrue = new DoubleFromJavaSensor(1.5);

		assertNotNull(new DoubleConditionalSensor(testBooleanConditionSensor, testDoubleSensorIfTrue, -0.3));
	}

	@Test
	public void testConstructorWithBooleanSensorDoubleValueAndDoubleSensorExpectingNotNullSimpleTest() {
		Sensor<Boolean> testBooleanConditionSensor = new BooleanFromJavaSensor(false);
		Sensor<Double> testDoubleSensorIfFalse = new DoubleFromJavaSensor(-0.3);

		assertNotNull(new DoubleConditionalSensor(testBooleanConditionSensor, 1.5, testDoubleSensorIfFalse));
	}

	@Test
	public void testConstructorWithBooleanSensorAndTwoDoubleValuesExpectingNotNullSimpleTest() {
		Sensor<Boolean> testBooleanConditionSensor = new BooleanFromJavaSensor(false);

		assertNotNull(new DoubleConditionalSensor(testBooleanConditionSensor, 1.5, -0.3));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoEqualSensorsExpectingTrue() {
		Sensor<Boolean> testBooleanConditionSensor = new BooleanFromJavaSensor(false);
		Sensor<Double> testDoubleSensorIfTrue = new DoubleFromJavaSensor(1.5);
		Sensor<Double> testDoubleSensorIfFalse = new DoubleFromJavaSensor(-0.3);

		DoubleConditionalSensor testDoubleConditionalSensor1 = new DoubleConditionalSensor(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		DoubleConditionalSensor testDoubleConditionalSensor2 = new DoubleConditionalSensor(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertTrue(testDoubleConditionalSensor1.equals(testDoubleConditionalSensor2));

		assertTrue(testDoubleConditionalSensor2.equals(testDoubleConditionalSensor1));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoDifferentSensorsExpectingFalse() {
		Sensor<Boolean> testBooleanConditionSensor1 = new BooleanFromJavaSensor(false);
		Sensor<Boolean> testBooleanConditionSensor2 = new BooleanFromJavaSensor(true);

		Sensor<Double> testDoubleSensorIfTrue = new DoubleFromJavaSensor(1.5);
		Sensor<Double> testDoubleSensorIfFalse = new DoubleFromJavaSensor(-0.3);

		DoubleConditionalSensor testDoubleConditionalSensor1 = new DoubleConditionalSensor(testBooleanConditionSensor1,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		DoubleConditionalSensor testDoubleConditionalSensor2 = new DoubleConditionalSensor(testBooleanConditionSensor2,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertFalse(testDoubleConditionalSensor1.equals(testDoubleConditionalSensor2));

		assertFalse(testDoubleConditionalSensor2.equals(testDoubleConditionalSensor1));
	}

	@Test
	public void testOverrideMethodIsAvailableWithAllThreeInnerSensorsAreAvailableExpectingTrue() {
		Sensor<Boolean> testBooleanConditionSensor = new BooleanFromJavaSensor(false);
		Sensor<Double> testDoubleSensorIfTrue = new DoubleFromJavaSensor(1.5);
		Sensor<Double> testDoubleSensorIfFalse = new DoubleFromJavaSensor(-0.3);

		assertTrue(testBooleanConditionSensor.isAvailable());
		assertTrue(testDoubleSensorIfTrue.isAvailable());
		assertTrue(testDoubleSensorIfFalse.isAvailable());

		DoubleConditionalSensor testDoubleConditionalSensor = new DoubleConditionalSensor(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertTrue(testDoubleConditionalSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableWithOneInnerSensorIsNotAvailableExpectingFalse() {
		MockSensor<Boolean> testBooleanConditionSensor = new MockSensor<Boolean>(null);
		testBooleanConditionSensor.setAvailable(false);

		assertFalse(testBooleanConditionSensor.isAvailable());

		Sensor<Double> testDoubleSensorIfTrue = new DoubleFromJavaSensor(1.5);
		Sensor<Double> testDoubleSensorIfFalse = new DoubleFromJavaSensor(-0.3);

		DoubleConditionalSensor testDoubleConditionalSensor = new DoubleConditionalSensor(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertFalse(testDoubleConditionalSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodToStringExpectingNotNullSimpleTest() {
		Sensor<Boolean> testBooleanConditionSensor = new BooleanFromJavaSensor(false);
		Sensor<Double> testDoubleSensorIfTrue = new DoubleFromJavaSensor(1.5);
		Sensor<Double> testDoubleSensorIfFalse = new DoubleFromJavaSensor(-0.3);

		DoubleConditionalSensor testDoubleConditionalSensor = new DoubleConditionalSensor(testBooleanConditionSensor,
				testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		assertNotNull(testDoubleConditionalSensor.toString());
	}
}