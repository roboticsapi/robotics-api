/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.sensor.DeviceBasedDoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleArrayFromDoubleSensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.runtime.mockclass.MockDeviceDriverImpl;
import org.roboticsapi.runtime.mockclass.MockDoubleArraySensor;
import org.roboticsapi.runtime.mockclass.MockDoubleSensor;

public abstract class AbstractDoubleArraySensorTest extends AbstractRuntimeTest {
	private class SpecialMockDoubleArraySensor extends MockDoubleArraySensor {
		public SpecialMockDoubleArraySensor(RoboticsRuntime runtime, int size) {
			super(runtime, size);
		}

		@Override
		public Double[] getDefaultValue() {
			return super.getDefaultValue();
		}
	}

	@Test(timeout = 15000)
	public void testGetDefaultValueOfClassDoubleArraySensorExpectingTestArrayOfLengthTen() {
		Double[] d = null;

		SpecialMockDoubleArraySensor testSensor = new SpecialMockDoubleArraySensor(getRuntime(), 10);

		d = testSensor.getDefaultValue();

		assertNotNull(d);
		assertEquals(10, d.length);
	}

	// Tests for class DeviceBasedDoubleArraySensor<T>:

	private class MockDeviceBasedDoubleArraySensor<T extends DeviceDriver> extends DeviceBasedDoubleArraySensor<T> {
		public MockDeviceBasedDoubleArraySensor(T driver, int size) {
			super(driver, size);
		}

		@Override
		public Double[] getDefaultValue() {
			return super.getDefaultValue();
		}
	}

	@Test(timeout = 5000)
	public void testGetDefaultValueOfClassDeviceBasedDoubleArraySensorExpectingTestArrayOfLengthFive() {
		Double[] d = null;

		DeviceDriver testDriver = new MockDeviceDriverImpl(getRuntime());
		MockDeviceBasedDoubleArraySensor<DeviceDriver> testSensor = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDriver, 5);

		d = testSensor.getDefaultValue();

		assertNotNull(d);
		assertEquals(5, d.length);
	}

	@Test(timeout = 5000)
	public void testGetDriverOfClassDeviceBasedDoubleArraySensorExpectingTestDriver() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(getRuntime());

		DeviceBasedDoubleArraySensor<DeviceDriver> testSensor = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDriver, 5);

		assertNotNull(testSensor.getDriver());
		assertEquals(testDriver, testSensor.getDriver());
	}

	@Test(timeout = 5000)
	public void testOverrideMethodEqualsWithTwoEqualTestDeviceBasedDoubleArraySensorsExpectingTrue() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(getRuntime());

		DeviceBasedDoubleArraySensor<DeviceDriver> testSensor1 = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDriver, 5);
		DeviceBasedDoubleArraySensor<DeviceDriver> testSensor2 = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDriver, 5);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test(timeout = 5000)
	public void testOverrideMethodEqualsWithTwoDifferentTestDeviceBasedDoubleArraySensorsExpectingFalse() {
		DeviceDriver testDriver1 = new MockDeviceDriverImpl(getRuntime());
		DeviceDriver testDriver2 = new MockDeviceDriverImpl(getRuntime());

		DeviceBasedDoubleArraySensor<DeviceDriver> testSensor1 = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDriver1, 5);
		DeviceBasedDoubleArraySensor<DeviceDriver> testSensor2 = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDriver2, 5);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test(timeout = 5000)
	public void testOverrideMethodEqualsWithTwoDifferentTestSensorsExpectingFalse() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(getRuntime());

		DeviceBasedDoubleArraySensor<DeviceDriver> testSensor1 = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDriver, 5);
		DoubleSensor testSensor2 = new DoubleFromJavaSensor(1);

		assertFalse(testSensor1.equals(testSensor2));
	}

	// Tests for class DoubleArrayFromDoubleSensor:

	@Test(timeout = 5000)
	public void testOverrideMethodGetSensorsExpectingTheArrayWithTheTwoTestInnerSensors() {
		DoubleSensor[] innerSensors = new DoubleSensor[2];
		innerSensors[0] = new DoubleFromJavaSensor(0);
		innerSensors[1] = new DoubleFromJavaSensor(1);

		DoubleArrayFromDoubleSensor testSensor = new DoubleArrayFromDoubleSensor(innerSensors);

		assertNotNull(testSensor.getSensors());

		assertEquals(innerSensors.length, testSensor.getSensors().length);

		assertSame(innerSensors, testSensor.getSensors());

		assertEquals(innerSensors[0], testSensor.getSensors()[0]);
		assertEquals(innerSensors[1], testSensor.getSensors()[1]);
	}

	@Test(timeout = 5000)
	public void testOverrideMethodEqualsWithTwoEqualDoubleArrayFromDoubleSensorsExpectingTrue() {
		DoubleSensor[] innerSensors = new DoubleSensor[2];
		innerSensors[0] = new DoubleFromJavaSensor(0);
		innerSensors[1] = new DoubleFromJavaSensor(1);

		DoubleArrayFromDoubleSensor testSensor1 = new DoubleArrayFromDoubleSensor(innerSensors);
		DoubleArrayFromDoubleSensor testSensor2 = new DoubleArrayFromDoubleSensor(innerSensors);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test(timeout = 5000)
	public void testOverrideMethodEqualsWithTwoDifferentDoubleArrayFromDoubleSensorsExpectingFalse() {
		DoubleSensor[] innerSensors1 = new DoubleSensor[1];
		innerSensors1[0] = new DoubleFromJavaSensor(0);

		DoubleSensor[] innerSensors2 = new DoubleSensor[2];
		innerSensors2[0] = new DoubleFromJavaSensor(0);
		innerSensors2[1] = new DoubleFromJavaSensor(1);

		DoubleArrayFromDoubleSensor testSensor1 = new DoubleArrayFromDoubleSensor(innerSensors1);
		DoubleArrayFromDoubleSensor testSensor2 = new DoubleArrayFromDoubleSensor(innerSensors2);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test(timeout = 5000)
	public void testOverrideMethodEqualsWithTwoDifferentSensorsExpectingFalse() {
		DoubleSensor[] innerSensors = new DoubleSensor[2];
		innerSensors[0] = new DoubleFromJavaSensor(0);
		innerSensors[1] = new DoubleFromJavaSensor(1);

		DoubleArrayFromDoubleSensor testSensor1 = new DoubleArrayFromDoubleSensor(innerSensors);

		DoubleSensor testSensor2 = new DoubleFromJavaSensor(2);

		assertFalse(testSensor1.equals(testSensor2));
	}

	@Test(timeout = 5000)
	public void testOverrideMethodIsAvailableExpectingTrue() {
		DoubleSensor[] innerSensors = new DoubleSensor[2];
		innerSensors[0] = new DoubleFromJavaSensor(0);
		innerSensors[1] = new DoubleFromJavaSensor(1);

		DoubleArrayFromDoubleSensor testSensor = new DoubleArrayFromDoubleSensor(innerSensors);

		assertTrue(testSensor.isAvailable());
	}

	@Test(timeout = 5000)
	public void testOverrideMethodIsAvailableWithNonavailableInnerSensorExpectingFalse() {
		MockDoubleSensor[] innerSensors = new MockDoubleSensor[1];
		innerSensors[0] = new MockDoubleSensor(getRuntime());

		innerSensors[0].setAvailable(false);

		DoubleArrayFromDoubleSensor testSensor = new DoubleArrayFromDoubleSensor(innerSensors);

		assertFalse(testSensor.isAvailable());
	}

	@Test(timeout = 5000)
	public void testOverrideMethodToStringExpectingNotNull() {
		DoubleSensor[] innerSensors = new DoubleSensor[2];
		innerSensors[0] = new DoubleFromJavaSensor(0);
		innerSensors[1] = new DoubleFromJavaSensor(1);

		DoubleArrayFromDoubleSensor testSensor = new DoubleArrayFromDoubleSensor(innerSensors);

		assertNotNull(testSensor.toString());
	}
}
