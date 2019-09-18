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
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.TestDeviceDriver;
import org.roboticsapi.core.mockclass.MockDeviceDriverImpl;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

public class DriverBasedRealtimeBooleanTest {

	private class MockDeviceBasedBooleanSensor<T extends DeviceDriver> extends DriverBasedRealtimeBoolean<T> {
		public MockDeviceBasedBooleanSensor(T parent) {
			super(parent);
		}
	}

	@Test
	public void testOverrideMethodEqualsOfClassDeviceBasedBooleanSensorWithTwoEqualSensorsExpectingTrue() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(null, null);

		RealtimeBoolean testSensor1 = new MockDeviceBasedBooleanSensor<DeviceDriver>(testDriver);
		RealtimeBoolean testSensor2 = new MockDeviceBasedBooleanSensor<DeviceDriver>(testDriver);

		assertTrue(testSensor1.equals(testSensor2));
		assertTrue(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassDeviceBasedBooleanSensorWithTwoSensorsWithDifferentDriversExpectingFalse() {
		DeviceDriver testDriver1 = new MockDeviceDriverImpl(null, null);
		DeviceDriver testDriver2 = new TestDeviceDriver(null, null);

		RealtimeBoolean testSensor1 = new MockDeviceBasedBooleanSensor<DeviceDriver>(testDriver1);
		RealtimeBoolean testSensor2 = new MockDeviceBasedBooleanSensor<DeviceDriver>(testDriver2);

		assertFalse(testSensor1.equals(testSensor2));
		assertFalse(testSensor2.equals(testSensor1));
	}

	@Test
	public void testOverrideMethodEqualsOfClassDeviceBasedBooleanSensorWithTwoDifferentSensorsExpectingFalse() {
		DeviceDriver testDriver = new MockDeviceDriverImpl(null, null);

		RealtimeBoolean testBoolSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(testDriver);
		RealtimeInteger testIntSensor = RealtimeInteger.createWritable(2);

		assertFalse(testBoolSensor.equals(testIntSensor));
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassDeviceBasedBooleanSensorWithPresentDriverExpectingTrue() {
		MockDeviceDriverImpl testDriver = new MockDeviceDriverImpl(null, null);
		testDriver.setPresent(true);

		MockDeviceBasedBooleanSensor<DeviceDriver> testSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver);

		assertTrue(testSensor.isAvailable());
	}

	@Test
	public void testOverrideMethodIsAvailableOfClassDeviceBasedBooleanSensorWithNonpresentDriverExpectingFalse() {
		MockDeviceDriverImpl testDriver = new MockDeviceDriverImpl(null, null);
		testDriver.setPresent(false);

		MockDeviceBasedBooleanSensor<DeviceDriver> testSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDriver);

		assertFalse(testSensor.isAvailable());
	}
}
