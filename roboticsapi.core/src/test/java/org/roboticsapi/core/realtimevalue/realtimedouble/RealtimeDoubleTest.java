/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.mockclass.MockDeviceDriverImpl;
import org.roboticsapi.core.mockclass.MockRoboticsRuntime;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeDouble;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeDoubleArray;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

// Tests for class RealtimeDouble and its subclasses *Double*Sensor
public class RealtimeDoubleTest {
	// Tests for class AbsRealtimeDouble:

	@Test
	public void testOverrideMethodToStringOfSubclassAbsRealtimeDoubleExpectingNeitherNullNorEmptySimpleTest() {
		RealtimeDouble testAbsRealtimeDouble = RealtimeDouble.createAbsolute((new WritableRealtimeDouble(0)));

		assertNotNull(testAbsRealtimeDouble.toString());

		assertFalse(testAbsRealtimeDouble.toString().isEmpty());
	}

	// Tests for class AddedRealtimeDouble:

	@Test
	public void testOverrideMethodToStringOfSubclassAddedRealtimeDoubleExpectingNeitherNullNorEmptySimpleTest() {
		AddedRealtimeDouble testAddedRealtimeDouble = new AddedRealtimeDouble(new WritableRealtimeDouble(0),
				new WritableRealtimeDouble(1));

		assertNotNull(testAddedRealtimeDouble.toString());

		assertFalse(testAddedRealtimeDouble.toString().isEmpty());
	}

	// Tests for class Atan2RealtimeDouble:

	@Test
	public void testGetYOfSubclassAtan2RealtimeDoubleWithTestSensorExpectingTheTestSensorSimpleTest() {
		RealtimeDouble testYSensor = new WritableRealtimeDouble(2);

		Atan2RealtimeDouble testAtan2RealtimeDouble = new Atan2RealtimeDouble(testYSensor,
				new WritableRealtimeDouble(1));

		assertNotNull(testAtan2RealtimeDouble.getY());

		assertEquals(testYSensor, testAtan2RealtimeDouble.getY());
	}

	@Test
	public void testGetXOfSubclassAtan2RealtimeDoubleWithTestSensorExpectingTheTestSensorSimpleTest() {
		RealtimeDouble testXSensor = new WritableRealtimeDouble(1);

		Atan2RealtimeDouble testAtan2RealtimeDouble = new Atan2RealtimeDouble(new WritableRealtimeDouble(2),
				testXSensor);

		assertNotNull(testAtan2RealtimeDouble.getX());

		assertEquals(testXSensor, testAtan2RealtimeDouble.getX());
	}

	@Test
	public void testOverrideMethodToStringOfSubclassAtan2RealtimeDoubleExpectingNeitherNullNorEmptySimpleTest() {
		Atan2RealtimeDouble testAtan2RealtimeDouble = new Atan2RealtimeDouble(new WritableRealtimeDouble(0),
				new WritableRealtimeDouble(1));

		assertNotNull(testAtan2RealtimeDouble.toString());

		assertFalse(testAtan2RealtimeDouble.toString().isEmpty());
	}

	// Tests for class DriverBasedRealtimeDouble:

	private class MockDeviceBasedRealtimeDouble<T extends DeviceDriver> extends DriverBasedRealtimeDouble<T> {
		public MockDeviceBasedRealtimeDouble(T driver) {
			super(driver);
		}
	}

	@Test
	public void testOverrideMethodEqualsOfSubclassDeviceBasedRealtimeDoubleWithTwoEqualSensorsExpectingTrue() {
		RoboticsRuntime testRoboticsRuntime = new MockRoboticsRuntime();
		DeviceDriver testDeviceDriver = new MockDeviceDriverImpl(null, testRoboticsRuntime);

		RealtimeDouble testDeviceBasedRealtimeDouble1 = new MockDeviceBasedRealtimeDouble<DeviceDriver>(
				testDeviceDriver);
		RealtimeDouble testDeviceBasedRealtimeDouble2 = new MockDeviceBasedRealtimeDouble<DeviceDriver>(
				testDeviceDriver);

		assertTrue(testDeviceBasedRealtimeDouble1.equals(testDeviceBasedRealtimeDouble2));

		assertTrue(testDeviceBasedRealtimeDouble2.equals(testDeviceBasedRealtimeDouble1));
	}

	@Test
	public void testOverrideMethodEqualsOfSubclassDeviceBasedRealtimeDoubleWithTwoDifferentSensorsExpectingFalse() {
		RoboticsRuntime testRoboticsRuntime = new MockRoboticsRuntime();
		DeviceDriver testDeviceDriver1 = new MockDeviceDriverImpl(null, testRoboticsRuntime);
		DeviceDriver testDeviceDriver2 = new MockDeviceDriverImpl(null, testRoboticsRuntime);

		RealtimeDouble testDeviceBasedRealtimeDouble1 = new MockDeviceBasedRealtimeDouble<DeviceDriver>(
				testDeviceDriver1);
		RealtimeDouble testDeviceBasedRealtimeDouble2 = new MockDeviceBasedRealtimeDouble<DeviceDriver>(
				testDeviceDriver2);

		assertFalse(testDeviceBasedRealtimeDouble1.equals(testDeviceBasedRealtimeDouble2));

		assertFalse(testDeviceBasedRealtimeDouble2.equals(testDeviceBasedRealtimeDouble1));
	}

	// Tests for class DividedRealtimeDouble:

	@Test
	public void testOverrideMethodToStringOfSubclassDividedRealtimeDoubleExpectingNotNullSimpleTest() {
		RealtimeDouble testRealtimeDouble1 = new WritableRealtimeDouble(1);
		RealtimeDouble testRealtimeDouble2 = new WritableRealtimeDouble(2);

		DividedRealtimeDouble testDividedRealtimeDouble = new DividedRealtimeDouble(testRealtimeDouble1,
				testRealtimeDouble2);

		assertNotNull(testDividedRealtimeDouble.toString());
	}

	// Tests for class SquareRootRealtimeDouble:

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassSquareRootRealtimeDoubleWithTwoNonnegativeInnerSensorValuesExpectingPositiveSqrtValue() {
		WritableRealtimeDouble testInnerRealtimeDouble = new WritableRealtimeDouble(0);
		SquareRootRealtimeDouble testSquareRootRealtimeDouble = new SquareRootRealtimeDouble(testInnerRealtimeDouble);

		assertEquals(0, testSquareRootRealtimeDouble.getCheapValue(), 0.001);

		testInnerRealtimeDouble.setValue(25d);

		assertEquals(5, testSquareRootRealtimeDouble.getCheapValue(), 0.001);
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassSquareRootRealtimeDoubleWithNegativeInnerSensorValueExpectingNaN() {
		RealtimeDouble testInnerRealtimeDouble = new WritableRealtimeDouble(-2);
		SquareRootRealtimeDouble testSquareRootRealtimeDouble = new SquareRootRealtimeDouble(testInnerRealtimeDouble);

		assertTrue(testSquareRootRealtimeDouble.getCheapValue().equals(Double.NaN));
	}

	@Test
	public void testOverrideMethodToStringOfSubclassSquareRootRealtimeDoubleExpectingNotNullSimpleTest() {
		RealtimeDouble testRealtimeDouble = new WritableRealtimeDouble(1);
		SquareRootRealtimeDouble testSquareRootRealtimeDouble = new SquareRootRealtimeDouble(testRealtimeDouble);

		assertNotNull(testSquareRootRealtimeDouble.toString());
	}

	// Tests for class SlidingAverageRealtimeDouble:

	@Test
	public void testOverrideMethodEqualsOfClassSlidingAverageRealtimeDoubleWithTwoEqualSensorsExpectingTrue() {
		double testDuration = 1;
		RealtimeDouble innerSensor = new WritableRealtimeDouble(1);

		SlidingAverageRealtimeDouble testSensor1 = new SlidingAverageRealtimeDouble(innerSensor, testDuration);
		SlidingAverageRealtimeDouble testSensor2 = new SlidingAverageRealtimeDouble(innerSensor, testDuration);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassSlidingAverageRealtimeDoubleWithTwoSensorsWithDifferentInnerSensorsExpectingFalse() {
		double testDuration = 1;
		RealtimeDouble innerSensor1 = new WritableRealtimeDouble(1);
		RealtimeDouble innerSensor2 = new WritableRealtimeDouble(2);

		SlidingAverageRealtimeDouble testSensor1 = new SlidingAverageRealtimeDouble(innerSensor1, testDuration);
		SlidingAverageRealtimeDouble testSensor2 = new SlidingAverageRealtimeDouble(innerSensor2, testDuration);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassSlidingAverageRealtimeDoubleWithTwoDifferentSensorsExpectingFalse() {
		double testDuration = 1;
		RealtimeDouble innerSensor = new WritableRealtimeDouble(1);

		SlidingAverageRealtimeDouble testSlideAvgSensor = new SlidingAverageRealtimeDouble(innerSensor, testDuration);
		RealtimeDouble testDblSensor = new WritableRealtimeDouble(2);

		assertFalse(testSlideAvgSensor.equals(testDblSensor));
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassSlidingAverageRealtimeDoubleWithAvailableInnerSensorExpectingTrue() {
		double testDuration = 1;
		MockRealtimeDouble innerSensor = new MockRealtimeDouble(null);
		innerSensor.setAvailable(true);

		SlidingAverageRealtimeDouble testSensor = new SlidingAverageRealtimeDouble(innerSensor, testDuration);

		assertTrue(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassSlidingAverageRealtimeDoubleWithNonavailableInnerSensorExpectingFalse() {
		double testDuration = 1;
		MockRealtimeDouble innerSensor = new MockRealtimeDouble(null);
		innerSensor.setAvailable(false);

		SlidingAverageRealtimeDouble testSensor = new SlidingAverageRealtimeDouble(innerSensor, testDuration);

		assertFalse(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodToStringOfClassSlidingAverageRealtimeDoubleExpectingNotNull() {
		double testDuration = 1;
		MockRealtimeDouble innerSensor = new MockRealtimeDouble(null);

		SlidingAverageRealtimeDouble testSensor = new SlidingAverageRealtimeDouble(innerSensor, testDuration);

		assertNotNull(testSensor.toString());
	}

	// Tests for class DoubleFromDoubleArraySensor:

	@Test
	public void testOverrideMethodEqualsOfClassDoubleFromDoubleArraySensorWithEqualIndexesExpectingTrue() {
		int testSize = 3;
		RealtimeDoubleArray arraySensor = new MockRealtimeDoubleArray(null, testSize);

		int testIndex = 1;

		GetFromArrayRealtimeDouble testSensor1 = new GetFromArrayRealtimeDouble(arraySensor, testIndex);
		GetFromArrayRealtimeDouble testSensor2 = new GetFromArrayRealtimeDouble(arraySensor, testIndex);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassDoubleFromDoubleArraySensorWithDifferentIndexesExpectingFalse() {
		int testSize = 3;
		RealtimeDoubleArray arraySensor = new MockRealtimeDoubleArray(null, testSize);

		int testIndex1 = 1;
		int testIndex2 = 2;

		GetFromArrayRealtimeDouble testSensor1 = new GetFromArrayRealtimeDouble(arraySensor, testIndex1);
		GetFromArrayRealtimeDouble testSensor2 = new GetFromArrayRealtimeDouble(arraySensor, testIndex2);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodToStringOfClassDoubleFromDoubleArraySensorExpectingNotNull() {
		int testSize = 3;
		RealtimeDoubleArray arraySensor = new MockRealtimeDoubleArray(null, testSize);

		int testIndex = 1;

		GetFromArrayRealtimeDouble testSensor = new GetFromArrayRealtimeDouble(arraySensor, testIndex);

		assertNotNull(testSensor.toString());
	}

	@Test
	public void testIsNullHasExpectedStructure() {
		RealtimeDouble fromValue = RealtimeDouble.createWritable(1);
		RealtimeBoolean createIsNull = fromValue.isNull();

		Assert.assertTrue(createIsNull instanceof RealtimeDoubleIsNull);

		Assert.assertEquals(fromValue, ((RealtimeDoubleIsNull) createIsNull).getOther());

	}
}
