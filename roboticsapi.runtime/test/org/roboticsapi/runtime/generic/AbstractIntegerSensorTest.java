/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.AddedIntegerSensor;
import org.roboticsapi.core.sensor.DividedIntegerSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.core.sensor.IntegerSensor;
import org.roboticsapi.core.sensor.MultipliedIntegerSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.runtime.mockclass.MockDeviceBasedIntegerSensor;
import org.roboticsapi.runtime.mockclass.MockDeviceDriverImpl;

//Test case for class IntegerSensor and its subclasses that expect a Runtime instance:
public abstract class AbstractIntegerSensorTest extends AbstractRuntimeTest {
	// For test method
	// testGetCheapValueWithVariousIntegerValuesIncludingExtremeValues():
	private class OperandsAndResult {
		public final Integer leftOperand;
		public final Integer rightOperand;
		public final Integer result;

		public OperandsAndResult(Integer leftOperand, Integer rightOperand, Integer result) {
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
			this.result = result;
		}
	}

	// Tests for class DeviceBasedIntegerSensor<...>:

	private DeviceDriver mockDeviceDriver = null;

	private MockDeviceBasedIntegerSensor<DeviceDriver> mockDeviceBasedSensor = null;

	private void initMockDeviceBasedSensor() {
		mockDeviceDriver = new MockDeviceDriverImpl(getRuntime());
		mockDeviceBasedSensor = new MockDeviceBasedIntegerSensor<DeviceDriver>(mockDeviceDriver);
	}

	@Test(timeout = 3000)
	public void testGetDefaultValueSimpleTest() {
		initMockDeviceBasedSensor();

		assertSame(0, mockDeviceBasedSensor.getDefaultValue());
	}

	@Test(timeout = 3000)
	public void testGetDriverSimpleTest() {
		initMockDeviceBasedSensor();

		assertSame(mockDeviceDriver, mockDeviceBasedSensor.getDriver());
	}

	@Test(timeout = 3000)
	public void testEqualsWithAnotherDeviceBasedIntegerSensorSimpleTest() {
		initMockDeviceBasedSensor();

		MockDeviceBasedIntegerSensor<DeviceDriver> testSensor = new MockDeviceBasedIntegerSensor<DeviceDriver>(
				mockDeviceDriver);

		assertTrue(mockDeviceBasedSensor.equals(testSensor));
	}

	@Test(timeout = 3000)
	public void testHashCodeByCallingOnly() {
		initMockDeviceBasedSensor();

		mockDeviceBasedSensor.hashCode();
	}

	@Test(timeout = 3000)
	public void testIsAvailableSimpleTest() {
		initMockDeviceBasedSensor();

		assertFalse(mockDeviceBasedSensor.isAvailable());
	}

	// Tests for class AddedIntegerSensor:

	@Test(timeout = 15000)
	public void testAddedIntegerSensorGetCurrentValueAndTestGetCheapValueWithVariousIntegerValuesIncludingExtremeValues()
			throws RoboticsException, InterruptedException {
		final int SLEEPTIME_IN_MILLISECONDS = 300;

		// define test values:
		final Integer[] LEFT_VALUES = new Integer[] { 0, 1, -10, Integer.MAX_VALUE, 0, Integer.MIN_VALUE, 0,
				Integer.MIN_VALUE, -1, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MIN_VALUE };
		final Integer[] RIGHT_VALUES = new Integer[] { 0, 1, -10, 0, Integer.MAX_VALUE, 0, Integer.MIN_VALUE, -1,
				Integer.MIN_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE };

		if (LEFT_VALUES.length != RIGHT_VALUES.length) {
			fail("The lengths of LEFT_VALUES and RIGHT_VALUES are not equal. " + "Test will be aborted.");
		}

		final int VALUES_COUNT = LEFT_VALUES.length;

		// define Sensors:
		IntegerFromJavaSensor testLeftSensor = new IntegerFromJavaSensor(0);
		IntegerFromJavaSensor testRightSensor = new IntegerFromJavaSensor(0);

		AddedIntegerSensor testAddedSensor = new AddedIntegerSensor(testLeftSensor, testRightSensor);

		final OperandsAndResult[] results = new OperandsAndResult[VALUES_COUNT];

		// measure procedure:
		Integer lastSetLeftValue = 0;
		Integer lastSetRightValue = 0;

		for (int i = 0; i < VALUES_COUNT; i++) {
			lastSetLeftValue = LEFT_VALUES[i];
			testLeftSensor.setValue(lastSetLeftValue);
			assertEquals(lastSetLeftValue, testLeftSensor.getCurrentValue());

			lastSetRightValue = RIGHT_VALUES[i];
			testRightSensor.setValue(lastSetRightValue);
			assertEquals(lastSetRightValue, testRightSensor.getCurrentValue());

			results[i] = new OperandsAndResult(lastSetLeftValue, lastSetRightValue, testAddedSensor.getCurrentValue());

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		// test 1: method getCurrentValue():
		// compare test values with computed values:
		Integer computedResult = 0;
		for (OperandsAndResult res : results) {
			computedResult = res.leftOperand + res.rightOperand; // add

			assertEquals(computedResult, res.result);
		}

		final Integer[] cheapValues = new Integer[VALUES_COUNT];

		// measure procedure for cheapValues:
		testLeftSensor.setValue(0);
		testRightSensor.setValue(0);

		lastSetLeftValue = 0;
		lastSetRightValue = 0;

		for (int i = 0; i < VALUES_COUNT; i++) {
			lastSetLeftValue = LEFT_VALUES[i];
			testLeftSensor.setValue(lastSetLeftValue);
			assertEquals(lastSetLeftValue, testLeftSensor.getCheapValue());

			lastSetRightValue = RIGHT_VALUES[i];
			testRightSensor.setValue(lastSetRightValue);
			assertEquals(lastSetRightValue, testRightSensor.getCheapValue());

			cheapValues[i] = testAddedSensor.getCheapValue();

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		// test 2: method getCheapValue():
		// compare test values with cheapValues:
		for (int i = 0; i < VALUES_COUNT; i++) {
			assertEquals(results[i].result, cheapValues[i]);
		}
	}

	// Tests for class MultipliedIntegerSensor:

	@Test(timeout = 15000)
	public void testMultipliedIntegerSensorGetCurrentValueAndTestGetCheapValueWithVariousIntegerValuesIncludingExtremeValues()
			throws RoboticsException, InterruptedException {
		final int SLEEPTIME_IN_MILLISECONDS = 300;

		// define test values:
		final Integer[] LEFT_VALUES = new Integer[] { 0, 1, 2, -10, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 2,
				Integer.MAX_VALUE, 100 };
		final Integer[] RIGHT_VALUES = new Integer[] { 1, 0, 2, -10, 1, Integer.MIN_VALUE, 2, Integer.MIN_VALUE, 100,
				Integer.MIN_VALUE };

		if (LEFT_VALUES.length != RIGHT_VALUES.length) {
			fail("The lengths of LEFT_VALUES and RIGHT_VALUES are not equal. " + "Test will be aborted.");
		}

		final int VALUES_COUNT = LEFT_VALUES.length;

		// define Sensors:
		IntegerFromJavaSensor testLeftSensor = new IntegerFromJavaSensor(0);
		IntegerFromJavaSensor testRightSensor = new IntegerFromJavaSensor(0);

		MultipliedIntegerSensor testMultipliedSensor = new MultipliedIntegerSensor(testLeftSensor, testRightSensor);

		final OperandsAndResult[] results = new OperandsAndResult[VALUES_COUNT];

		// measure procedure:
		Integer lastSetLeftValue = 0;
		Integer lastSetRightValue = 0;

		for (int i = 0; i < VALUES_COUNT; i++) {
			lastSetLeftValue = LEFT_VALUES[i];
			testLeftSensor.setValue(lastSetLeftValue);
			assertEquals(lastSetLeftValue, testLeftSensor.getCurrentValue());

			lastSetRightValue = RIGHT_VALUES[i];
			testRightSensor.setValue(lastSetRightValue);
			assertEquals(lastSetRightValue, testRightSensor.getCurrentValue());

			results[i] = new OperandsAndResult(lastSetLeftValue, lastSetRightValue,
					testMultipliedSensor.getCurrentValue());

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		// test 1: method getCurrentValue():
		// compare test values with computed values:
		Integer computedResult = 0;
		for (OperandsAndResult res : results) {
			computedResult = res.leftOperand * res.rightOperand; // multiply

			assertEquals(computedResult, res.result);
		}

		final Integer[] cheapValues = new Integer[VALUES_COUNT];

		// measure procedure for cheapValues:
		testLeftSensor.setValue(0);
		testRightSensor.setValue(0);

		lastSetLeftValue = 0;
		lastSetRightValue = 0;

		for (int i = 0; i < VALUES_COUNT; i++) {
			lastSetLeftValue = LEFT_VALUES[i];
			testLeftSensor.setValue(lastSetLeftValue);
			assertEquals(lastSetLeftValue, testLeftSensor.getCheapValue());

			lastSetRightValue = RIGHT_VALUES[i];
			testRightSensor.setValue(lastSetRightValue);
			assertEquals(lastSetRightValue, testRightSensor.getCheapValue());

			cheapValues[i] = testMultipliedSensor.getCheapValue();

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		// test 2: method getCheapValue():
		// compare test values with cheapValues:
		for (int i = 0; i < VALUES_COUNT; i++) {
			assertEquals(results[i].result, cheapValues[i]);
		}
	}

	// Tests for class DividedIntegerSensor:

	@Test(timeout = 15000)
	public void testDividedIntegerSensorGetCurrentValueAndTestGetCheapValueWithVariousIntegerValuesIncludingExtremeValues()
			throws SensorReadException, InterruptedException {
		final int SLEEPTIME_IN_MILLISECONDS = 300;

		// define test values:
		final Integer[] LEFT_VALUES = new Integer[] { 0, 10, 100, 1, Integer.MAX_VALUE, Integer.MIN_VALUE,
				Integer.MAX_VALUE, 2 * Integer.MAX_VALUE, Integer.MIN_VALUE, 3, -4 };
		final Integer[] RIGHT_VALUES = new Integer[] { 1, 10, 20, 2, 1, 2, Integer.MIN_VALUE, 1, -1, 2, 3 };

		if (LEFT_VALUES.length != RIGHT_VALUES.length) {
			fail("The lengths of LEFT_VALUES and RIGHT_VALUES are not equal. " + "Test will be aborted.");
		}

		final int VALUES_COUNT = LEFT_VALUES.length;

		// define Sensors:
		IntegerFromJavaSensor testLeftSensor = new IntegerFromJavaSensor(0);
		IntegerFromJavaSensor testRightSensor = new IntegerFromJavaSensor(0);

		DividedIntegerSensor testDividedSensor = new DividedIntegerSensor(testLeftSensor, testRightSensor);

		final OperandsAndResult[] results = new OperandsAndResult[VALUES_COUNT];

		// measure procedure:
		Integer lastSetLeftValue = 0;
		Integer lastSetRightValue = 0;

		for (int i = 0; i < VALUES_COUNT; i++) {
			lastSetLeftValue = LEFT_VALUES[i];
			testLeftSensor.setValue(lastSetLeftValue);
			assertEquals(lastSetLeftValue, testLeftSensor.getCurrentValue());

			lastSetRightValue = RIGHT_VALUES[i];
			testRightSensor.setValue(lastSetRightValue);
			assertEquals(lastSetRightValue, testRightSensor.getCurrentValue());

			results[i] = new OperandsAndResult(lastSetLeftValue, lastSetRightValue,
					testDividedSensor.getCurrentValue());

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		// test 1: method getCurrentValue():
		// compare test values with computed values:
		Integer computedResult = 0;
		for (OperandsAndResult res : results) {
			computedResult = res.leftOperand / res.rightOperand; // divide

			assertEquals(computedResult, res.result);
		}

		final Integer[] cheapValues = new Integer[VALUES_COUNT];

		// measure procedure for cheapValues:
		testLeftSensor.setValue(0);
		testRightSensor.setValue(0);

		lastSetLeftValue = 0;
		lastSetRightValue = 0;

		for (int i = 0; i < VALUES_COUNT; i++) {
			lastSetLeftValue = LEFT_VALUES[i];
			testLeftSensor.setValue(lastSetLeftValue);
			assertEquals(lastSetLeftValue, testLeftSensor.getCheapValue());

			lastSetRightValue = RIGHT_VALUES[i];
			testRightSensor.setValue(lastSetRightValue);
			assertEquals(lastSetRightValue, testRightSensor.getCheapValue());

			cheapValues[i] = testDividedSensor.getCheapValue();

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		// test 2: method getCheapValue():
		// compare test values with cheapValues:
		for (int i = 0; i < VALUES_COUNT; i++) {
			assertEquals(results[i].result, cheapValues[i]);
		}
	}

	@Test(timeout = 3000, expected = ArithmeticException.class)
	public void testDividedIntegerSensorGetCurrentValueWithZeroDivisorExpectingException() throws SensorReadException {
		IntegerSensor dividend = new IntegerFromJavaSensor(1);
		IntegerSensor divisor = new IntegerFromJavaSensor(0);
		DividedIntegerSensor testDividedSensor = new DividedIntegerSensor(dividend, divisor);

		testDividedSensor.getCurrentValue();
	}

	@Test(timeout = 3000, expected = ArithmeticException.class)
	public void testDividedIntegerSensorGetCheapValueWithZeroDivisorExpectingException() throws SensorReadException {
		IntegerSensor dividend = new IntegerFromJavaSensor(1);
		IntegerSensor divisor = new IntegerFromJavaSensor(0);
		DividedIntegerSensor testDividedSensor = new DividedIntegerSensor(dividend, divisor);

		testDividedSensor.getCheapValue();
	}
}
