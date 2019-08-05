/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.AbsDoubleSensor;
import org.roboticsapi.core.sensor.AddedDoubleSensor;
import org.roboticsapi.core.sensor.Atan2DoubleSensor;
import org.roboticsapi.core.sensor.DeviceBasedDoubleSensor;
import org.roboticsapi.core.sensor.DividedDoubleSensor;
import org.roboticsapi.core.sensor.DoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleFromDoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SlidingAverageDoubleSensor;
import org.roboticsapi.core.sensor.SquareRootDoubleSensor;
import org.roboticsapi.mockclass.MockDeviceDriverImpl;
import org.roboticsapi.mockclass.MockDoubleArraySensor;
import org.roboticsapi.mockclass.MockRoboticsRuntime;
import org.roboticsapi.mockclass.MockSensor;

// Tests for class DoubleSensor and its subclasses *Double*Sensor
public class DoubleSensorsTest {
	// Tests for class AbsDoubleSensor:

	@Test
	public void testOverrideMethodToStringOfSubclassAbsDoubleSensorExpectingNeitherNullNorEmptySimpleTest() {
		AbsDoubleSensor testAbsDoubleSensor = new AbsDoubleSensor(new DoubleFromJavaSensor(0));

		assertNotNull(testAbsDoubleSensor.toString());

		assertFalse(testAbsDoubleSensor.toString().isEmpty());
	}

	// Tests for class AddedDoubleSensor:

	@Test
	public void testOverrideMethodToStringOfSubclassAddedDoubleSensorExpectingNeitherNullNorEmptySimpleTest() {
		AddedDoubleSensor testAddedDoubleSensor = new AddedDoubleSensor(new DoubleFromJavaSensor(0),
				new DoubleFromJavaSensor(1));

		assertNotNull(testAddedDoubleSensor.toString());

		assertFalse(testAddedDoubleSensor.toString().isEmpty());
	}

	// Tests for class Atan2DoubleSensor:

	@Test
	public void testGetYOfSubclassAtan2DoubleSensorWithTestSensorExpectingTheTestSensorSimpleTest() {
		DoubleSensor testYSensor = new DoubleFromJavaSensor(2);

		Atan2DoubleSensor testAtan2DoubleSensor = new Atan2DoubleSensor(testYSensor, new DoubleFromJavaSensor(1));

		assertNotNull(testAtan2DoubleSensor.getY());

		assertEquals(testYSensor, testAtan2DoubleSensor.getY());
	}

	@Test
	public void testGetXOfSubclassAtan2DoubleSensorWithTestSensorExpectingTheTestSensorSimpleTest() {
		DoubleSensor testXSensor = new DoubleFromJavaSensor(1);

		Atan2DoubleSensor testAtan2DoubleSensor = new Atan2DoubleSensor(new DoubleFromJavaSensor(2), testXSensor);

		assertNotNull(testAtan2DoubleSensor.getX());

		assertEquals(testXSensor, testAtan2DoubleSensor.getX());
	}

	@Test
	public void testOverrideMethodToStringOfSubclassAtan2DoubleSensorExpectingNeitherNullNorEmptySimpleTest() {
		Atan2DoubleSensor testAtan2DoubleSensor = new Atan2DoubleSensor(new DoubleFromJavaSensor(0),
				new DoubleFromJavaSensor(1));

		assertNotNull(testAtan2DoubleSensor.toString());

		assertFalse(testAtan2DoubleSensor.toString().isEmpty());
	}

	// Tests for class DeviceBasedDoubleSensor:

	private class MockDeviceBasedDoubleSensor<T extends DeviceDriver> extends DeviceBasedDoubleSensor<T> {
		public MockDeviceBasedDoubleSensor(T driver) {
			super(driver);
		}

		@Override
		public Double getDefaultValue() {
			return super.getDefaultValue();
		}
	}

	@Test
	public void testOverrideMethodEqualsOfSubclassDeviceBasedDoubleSensorWithTwoEqualSensorsExpectingTrue() {
		RoboticsRuntime testRoboticsRuntime = new MockRoboticsRuntime();
		DeviceDriver testDeviceDriver = new MockDeviceDriverImpl(testRoboticsRuntime);

		DeviceBasedDoubleSensor<DeviceDriver> testDeviceBasedDoubleSensor1 = new MockDeviceBasedDoubleSensor<DeviceDriver>(
				testDeviceDriver);
		DeviceBasedDoubleSensor<DeviceDriver> testDeviceBasedDoubleSensor2 = new MockDeviceBasedDoubleSensor<DeviceDriver>(
				testDeviceDriver);

		assertTrue(testDeviceBasedDoubleSensor1.equals(testDeviceBasedDoubleSensor2));

		assertTrue(testDeviceBasedDoubleSensor2.equals(testDeviceBasedDoubleSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfSubclassDeviceBasedDoubleSensorWithTwoDifferentSensorsExpectingFalse() {
		RoboticsRuntime testRoboticsRuntime = new MockRoboticsRuntime();
		DeviceDriver testDeviceDriver1 = new MockDeviceDriverImpl(testRoboticsRuntime);
		DeviceDriver testDeviceDriver2 = new MockDeviceDriverImpl(testRoboticsRuntime);

		DeviceBasedDoubleSensor<DeviceDriver> testDeviceBasedDoubleSensor1 = new MockDeviceBasedDoubleSensor<DeviceDriver>(
				testDeviceDriver1);
		DeviceBasedDoubleSensor<DeviceDriver> testDeviceBasedDoubleSensor2 = new MockDeviceBasedDoubleSensor<DeviceDriver>(
				testDeviceDriver2);

		assertFalse(testDeviceBasedDoubleSensor1.equals(testDeviceBasedDoubleSensor2));

		assertFalse(testDeviceBasedDoubleSensor2.equals(testDeviceBasedDoubleSensor1));
	}

	@Test
	public void testOverrideMethodGetDefaultValueOfSubclassDeviceBasedDoubleSensorExpectingDefaultValueZero() {
		MockDeviceBasedDoubleSensor<DeviceDriver> testDeviceBasedDoubleSensor = new MockDeviceBasedDoubleSensor<DeviceDriver>(
				new MockDeviceDriverImpl(null));

		testDeviceBasedDoubleSensor.getDefaultValue();

		assertEquals(0d, testDeviceBasedDoubleSensor.getDefaultValue(), 0.000001);
	}

	// Tests for class DividedDoubleSensor:

	@Test
	public void testOverrideMethodToStringOfSubclassDividedDoubleSensorExpectingNotNullSimpleTest() {
		DoubleSensor testDoubleSensor1 = new DoubleFromJavaSensor(1);
		DoubleSensor testDoubleSensor2 = new DoubleFromJavaSensor(2);

		DividedDoubleSensor testDividedDoubleSensor = new DividedDoubleSensor(testDoubleSensor1, testDoubleSensor2);

		assertNotNull(testDividedDoubleSensor.toString());
	}

	// Tests for class SquareRootDoubleSensor:

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassSquareRootDoubleSensorWithTwoNonnegativeInnerSensorValuesExpectingPositiveSqrtValue() {
		DoubleFromJavaSensor testInnerDoubleSensor = new DoubleFromJavaSensor(0);
		SquareRootDoubleSensor testSquareRootDoubleSensor = new SquareRootDoubleSensor(testInnerDoubleSensor);

		assertEquals(0, testSquareRootDoubleSensor.getCheapValue(), 0.001);

		testInnerDoubleSensor.setValue(25);

		assertEquals(5, testSquareRootDoubleSensor.getCheapValue(), 0.001);
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassSquareRootDoubleSensorWithNegativeInnerSensorValueExpectingNaN() {
		DoubleSensor testInnerDoubleSensor = new DoubleFromJavaSensor(-2);
		SquareRootDoubleSensor testSquareRootDoubleSensor = new SquareRootDoubleSensor(testInnerDoubleSensor);

		assertTrue(testSquareRootDoubleSensor.getCheapValue().equals(Double.NaN));
	}

	@Test
	public void testOverrideMethodToStringOfSubclassSquareRootDoubleSensorExpectingNotNullSimpleTest() {
		DoubleSensor testDoubleSensor = new DoubleFromJavaSensor(1);
		SquareRootDoubleSensor testSquareRootDoubleSensor = new SquareRootDoubleSensor(testDoubleSensor);

		assertNotNull(testSquareRootDoubleSensor.toString());
	}

	// Tests for class SlidingAverageDoubleSensor:

	@Test
	public void testOverrideMethodEqualsOfClassSlidingAverageDoubleSensorWithTwoEqualSensorsExpectingTrue() {
		double testDuration = 1;
		Sensor<Double> innerSensor = new DoubleFromJavaSensor(1);

		SlidingAverageDoubleSensor testSensor1 = new SlidingAverageDoubleSensor(innerSensor, testDuration);
		SlidingAverageDoubleSensor testSensor2 = new SlidingAverageDoubleSensor(innerSensor, testDuration);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassSlidingAverageDoubleSensorWithTwoSensorsWithDifferentInnerSensorsExpectingFalse() {
		double testDuration = 1;
		Sensor<Double> innerSensor1 = new DoubleFromJavaSensor(1);
		Sensor<Double> innerSensor2 = new DoubleFromJavaSensor(2);

		SlidingAverageDoubleSensor testSensor1 = new SlidingAverageDoubleSensor(innerSensor1, testDuration);
		SlidingAverageDoubleSensor testSensor2 = new SlidingAverageDoubleSensor(innerSensor2, testDuration);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassSlidingAverageDoubleSensorWithTwoDifferentSensorsExpectingFalse() {
		double testDuration = 1;
		Sensor<Double> innerSensor = new DoubleFromJavaSensor(1);

		SlidingAverageDoubleSensor testSlideAvgSensor = new SlidingAverageDoubleSensor(innerSensor, testDuration);
		DoubleSensor testDblSensor = new DoubleFromJavaSensor(2);

		assertFalse(testSlideAvgSensor.equals(testDblSensor));
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassSlidingAverageDoubleSensorWithAvailableInnerSensorExpectingTrue() {
		double testDuration = 1;
		MockSensor<Double> innerSensor = new MockSensor<Double>(null);
		innerSensor.setAvailable(true);

		SlidingAverageDoubleSensor testSensor = new SlidingAverageDoubleSensor(innerSensor, testDuration);

		assertTrue(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassSlidingAverageDoubleSensorWithNonavailableInnerSensorExpectingFalse() {
		double testDuration = 1;
		MockSensor<Double> innerSensor = new MockSensor<Double>(null);
		innerSensor.setAvailable(false);

		SlidingAverageDoubleSensor testSensor = new SlidingAverageDoubleSensor(innerSensor, testDuration);

		assertFalse(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodToStringOfClassSlidingAverageDoubleSensorExpectingNotNull() {
		double testDuration = 1;
		Sensor<Double> innerSensor = new MockSensor<Double>(null);

		SlidingAverageDoubleSensor testSensor = new SlidingAverageDoubleSensor(innerSensor, testDuration);

		assertNotNull(testSensor.toString());
	}

	// Tests for class DoubleFromDoubleArraySensor:

	@Test
	public void testOverrideMethodEqualsOfClassDoubleFromDoubleArraySensorWithEqualIndexesExpectingTrue() {
		int testSize = 3;
		DoubleArraySensor arraySensor = new MockDoubleArraySensor(null, testSize);

		int testIndex = 1;

		DoubleFromDoubleArraySensor testSensor1 = new DoubleFromDoubleArraySensor(arraySensor, testIndex);
		DoubleFromDoubleArraySensor testSensor2 = new DoubleFromDoubleArraySensor(arraySensor, testIndex);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassDoubleFromDoubleArraySensorWithDifferentIndexesExpectingFalse() {
		int testSize = 3;
		DoubleArraySensor arraySensor = new MockDoubleArraySensor(null, testSize);

		int testIndex1 = 1;
		int testIndex2 = 2;

		DoubleFromDoubleArraySensor testSensor1 = new DoubleFromDoubleArraySensor(arraySensor, testIndex1);
		DoubleFromDoubleArraySensor testSensor2 = new DoubleFromDoubleArraySensor(arraySensor, testIndex2);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodToStringOfClassDoubleFromDoubleArraySensorExpectingNotNull() {
		int testSize = 3;
		DoubleArraySensor arraySensor = new MockDoubleArraySensor(null, testSize);

		int testIndex = 1;

		DoubleFromDoubleArraySensor testSensor = new DoubleFromDoubleArraySensor(arraySensor, testIndex);

		assertNotNull(testSensor.toString());
	}
}
