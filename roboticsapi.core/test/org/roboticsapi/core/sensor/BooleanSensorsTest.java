/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.sensor.BooleanAtTimeSensor;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DeviceBasedBooleanSensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleIsGreaterSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.core.sensor.IntegerSensor;
import org.roboticsapi.core.sensor.NegatedBooleanSensor;
import org.roboticsapi.mockclass.MockBooleanSensor;
import org.roboticsapi.mockclass.MockDeviceDriverImpl;
import org.roboticsapi.mockclass.MockDoubleSensor;
import org.roboticsapi.mockclass.TestDeviceDriver;

// Tests for class BooleanSensor and its subclasses:
public class BooleanSensorsTest {
	// Tests for class BooleanSensor:

	class SpecialMockBooleanSensor extends MockBooleanSensor {
		public SpecialMockBooleanSensor(RoboticsRuntime runtime) {
			super(runtime);
		}

		@Override
		public Boolean getDefaultValue() {
			return super.getDefaultValue();
		}
	}

	@Test
	public void testOverrideMethodGetDefaultValueExpectingFalse() {
		SpecialMockBooleanSensor testSensor = new SpecialMockBooleanSensor(null);

		assertFalse(testSensor.getDefaultValue());
	}

	// Tests for subclass BooleanFromJavaSensor:

	@Test
	public void testOverrideToStringOfSubclassBooleanFromJavaSensorExpectingNotNullSimpleTest() {
		BooleanFromJavaSensor testSensor = new BooleanFromJavaSensor(false);
		assertNotNull(testSensor.toString());
	}

	// Tests for subclass BooleanAtTimeSensor:

	@Test
	public void testOverrideMethodEqualsWithTwoEqualBooleanAtTimeSensorsExpectingTrue() {
		BooleanSensor innerBooleanSensor = new BooleanFromJavaSensor(true);
		DoubleSensor innerDoubleSensor = new DoubleFromJavaSensor(1);
		final double MAX_AGE = 10;

		BooleanAtTimeSensor testSensor1 = new BooleanAtTimeSensor(innerBooleanSensor, innerDoubleSensor, MAX_AGE);
		BooleanAtTimeSensor testSensor2 = new BooleanAtTimeSensor(innerBooleanSensor, innerDoubleSensor, MAX_AGE);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoDifferentBooleanAtTimeSensorsExpectingFalse() {
		BooleanSensor innerBooleanSensor1 = new BooleanFromJavaSensor(true);
		BooleanSensor innerBooleanSensor2 = new BooleanFromJavaSensor(false);

		DoubleSensor innerDoubleSensor1 = new DoubleFromJavaSensor(1);
		DoubleSensor innerDoubleSensor2 = new DoubleFromJavaSensor(2);

		BooleanAtTimeSensor testSensor1 = new BooleanAtTimeSensor(innerBooleanSensor1, innerDoubleSensor1, 1);
		BooleanAtTimeSensor testSensor2 = new BooleanAtTimeSensor(innerBooleanSensor2, innerDoubleSensor2, 2);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoDifferentSensorsExpectingFalse() {
		BooleanSensor innerBooleanSensor = new BooleanFromJavaSensor(true);
		DoubleSensor innerDoubleSensor = new DoubleFromJavaSensor(1);

		BooleanAtTimeSensor testSensor = new BooleanAtTimeSensor(innerBooleanSensor, innerDoubleSensor, 10);

		IntegerSensor testIntegerSensor = new IntegerFromJavaSensor(2);

		assertFalse(testSensor.equals(testIntegerSensor));
	}

	@Test
	public void testOverrideMethodIsAvailableWithBothInnerSensorsAreAvailableExpectingTrue() {
		MockBooleanSensor innerBooleanSensor = new MockBooleanSensor(null);
		MockDoubleSensor innerDoubleSensor = new MockDoubleSensor(null);

		innerBooleanSensor.setAvailable(true);
		innerDoubleSensor.setAvailable(true);

		BooleanAtTimeSensor testSensor = new BooleanAtTimeSensor(innerBooleanSensor, innerDoubleSensor, 10);

		assertTrue(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableWithInnerSensorsAreNotAvailableExpectingTrue() {
		MockBooleanSensor innerBooleanSensor = new MockBooleanSensor(null);
		MockDoubleSensor innerDoubleSensor = new MockDoubleSensor(null);

		innerBooleanSensor.setAvailable(false);
		innerDoubleSensor.setAvailable(false);

		BooleanAtTimeSensor testSensor = new BooleanAtTimeSensor(innerBooleanSensor, innerDoubleSensor, 10);

		assertFalse(testSensor.isAvailable());
	}

	@Test
	public void testOverrideToStringOfSubclassBooleanAtTimeSensorExpectingNotNullSimpleTest() {
		BooleanSensor innerBooleanSensor = new BooleanFromJavaSensor(true);
		DoubleSensor innerDoubleSensor = new DoubleFromJavaSensor(1);

		BooleanAtTimeSensor testSensor = new BooleanAtTimeSensor(innerBooleanSensor, innerDoubleSensor, 10);

		assertNotNull(testSensor.toString());
	}

	// Tests for class NegatedBooleanSensor:

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassNegatedBooleanSensorExpectingNull() {
		BooleanSensor boolSensor = new MockBooleanSensor(null);
		NegatedBooleanSensor testSensor = new NegatedBooleanSensor(boolSensor);

		assertNull(testSensor.getCheapValue());
	}

	@Test
	public void testOverrideToStringOfSubclassNegatedBooleanSensorExpectingNotNull() {
		BooleanSensor boolSensor = new BooleanFromJavaSensor(true);
		NegatedBooleanSensor testSensor = new NegatedBooleanSensor(boolSensor);

		assertNotNull(testSensor.toString());
	}

	// Tests for class DoubleIsGreaterSensor:

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestValuesExpectingTrue() {
		DoubleIsGreaterSensor testSensor = new DoubleIsGreaterSensor(47.11, -0.42);

		assertTrue(testSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestValuesExpectingFalse() {
		DoubleIsGreaterSensor testSensor = new DoubleIsGreaterSensor(-0.4711, 42);

		assertFalse(testSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestSensorsExpectingTrue() {
		DoubleSensor leftSensor = new DoubleFromJavaSensor(47.11);
		DoubleSensor rightSensor = new DoubleFromJavaSensor(-0.42);

		DoubleIsGreaterSensor testSensor = new DoubleIsGreaterSensor(leftSensor, rightSensor);

		assertTrue(testSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueOfSubclassDoubleIsGreaterSensorWithTwoTestSensorsExpectingFalse() {
		DoubleSensor leftSensor = new DoubleFromJavaSensor(-0.4711);
		DoubleSensor rightSensor = new DoubleFromJavaSensor(42);

		DoubleIsGreaterSensor testSensor = new DoubleIsGreaterSensor(leftSensor, rightSensor);

		assertFalse(testSensor.getCheapValue());
	}

	// Tests for class DeviceBasedBooleanSensor:

	private class MockDeviceBasedBooleanSensor<T extends DeviceDriver> extends DeviceBasedBooleanSensor<T> {
		public MockDeviceBasedBooleanSensor(T parent) {
			super(parent);
		}

		@Override
		public Boolean getDefaultValue() {
			return super.getDefaultValue();
		}
	}

	@Test
	public void testOverrideMethodGetDefaultValueOfClassDeviceBasedBooleanSensorExpectingFalse() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(null);

		MockDeviceBasedBooleanSensor<DeviceDriver> testSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver);

		assertFalse(testSensor.getDefaultValue());
	}

	@Test
	public void testOverrideMethodEqualsOfClassDeviceBasedBooleanSensorWithTwoEqualSensorsExpectingTrue() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(null);

		DeviceBasedBooleanSensor<DeviceDriver> testSensor1 = new MockDeviceBasedBooleanSensor<DeviceDriver>(testDriver);
		DeviceBasedBooleanSensor<DeviceDriver> testSensor2 = new MockDeviceBasedBooleanSensor<DeviceDriver>(testDriver);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassDeviceBasedBooleanSensorWithTwoSensorsWithDifferentDriversExpectingFalse() {
		DeviceDriver testDriver1 = new MockDeviceDriverImpl(null);
		DeviceDriver testDriver2 = new TestDeviceDriver(null);

		DeviceBasedBooleanSensor<DeviceDriver> testSensor1 = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver1);
		DeviceBasedBooleanSensor<DeviceDriver> testSensor2 = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver2);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassDeviceBasedBooleanSensorWithTwoDifferentSensorsExpectingFalse() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(null);

		DeviceBasedBooleanSensor<DeviceDriver> testBoolSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver);
		IntegerSensor testIntSensor = new IntegerFromJavaSensor(2);

		assertFalse(testBoolSensor.equals(testIntSensor));
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassDeviceBasedBooleanSensorWithPresentDriverExpectingTrue() {
		MockDeviceDriverImpl testDriver = new MockDeviceDriverImpl(null);
		testDriver.setPresent(true);

		MockDeviceBasedBooleanSensor<DeviceDriver> testSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver);

		assertTrue(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassDeviceBasedBooleanSensorWithNonpresentDriverExpectingFalse() {
		MockDeviceDriverImpl testDriver = new MockDeviceDriverImpl(null);
		testDriver.setPresent(false);

		MockDeviceBasedBooleanSensor<DeviceDriver> testSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver);

		assertFalse(testSensor.isAvailable());
	}
}
