/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.AddedDoubleSensor;
import org.roboticsapi.core.sensor.Atan2DoubleSensor;
import org.roboticsapi.core.sensor.ConstantDoubleSensor;
import org.roboticsapi.core.sensor.DividedDoubleSensor;
import org.roboticsapi.core.sensor.MultipliedDoubleSensor;
import org.roboticsapi.core.sensor.PowerDoubleSensor;

//Tests for subclasses of class BinaryFunctionDoubleSensor:
@RunWith(Parameterized.class)
public abstract class AbstractBinaryFunctionDoubleSensorParameterizedTest extends AbstractRuntimeTest {
	@Rule
	public final TestRule globalTimeout = Timeout.millis(3000);

	private final Double testLeftValue;
	private final Double testRightValue;

	private ConstantDoubleSensor leftSensor = null;
	private ConstantDoubleSensor rightSensor = null;

	public AbstractBinaryFunctionDoubleSensorParameterizedTest(Double left, Double right) {
		testLeftValue = left;
		testRightValue = right;
	}

	@Parameterized.Parameters(name = "{0}; {1}")
	public static Collection<Object[]> testValues() {
		final Double[] TEST_DOUBLE_VALUES = new Double[] { Double.MIN_VALUE, -5.2, 0d, 6.3, 1.7976931348623157E308,
				Double.NaN };

		Object[][] testValues = new Object[TEST_DOUBLE_VALUES.length * TEST_DOUBLE_VALUES.length][2];

		int len = TEST_DOUBLE_VALUES.length;

		// create cross product:
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				testValues[i * len + j] = new Object[] { TEST_DOUBLE_VALUES[i], TEST_DOUBLE_VALUES[j] };
			}
		}

		return Arrays.asList(testValues);
	}

	@Before
	public void setup() {
		leftSensor = new ConstantDoubleSensor(testLeftValue);
		rightSensor = new ConstantDoubleSensor(testRightValue);
	}

	@After
	public void teardown() {
		leftSensor = null;
		rightSensor = null;
	}

	// Tests for subclass AddedDoubleSensor:

	@Test
	public void testAddedDoubleSensorGetCheapValueCalculatesSum() {
		AddedDoubleSensor testAddedDoubleSensor = new AddedDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = testLeftValue + testRightValue;
		Double measuredValue = testAddedDoubleSensor.getCheapValue();

		String failMsg = testLeftValue + " + " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	@Test
	public void testAddedDoubleSensorRoboticsRuntimeEvaluationCalculatesSum()
			throws RoboticsException, InterruptedException {

		AddedDoubleSensor testAddedDoubleSensor = new AddedDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = testLeftValue + testRightValue;
		Double measuredValue = getRuntime().getSensorValue(testAddedDoubleSensor);

		String failMsg = testLeftValue + " + " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	// Tests for subclass MultipliedDoubleSensor:

	@Test
	public void testMultipliedDoubleSensorGetCheapValueCalculatesProduct() {
		MultipliedDoubleSensor testMultipliedDoubleSensor = new MultipliedDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = testLeftValue * testRightValue;
		Double measuredValue = testMultipliedDoubleSensor.getCheapValue();

		String failMsg = testLeftValue + " * " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	@Test
	public void testMultipliedDoubleSensorRoboticsRuntimeEvaluationCalculatesProduct()
			throws RoboticsException, InterruptedException {
		MultipliedDoubleSensor testMultipliedDoubleSensor = new MultipliedDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = testLeftValue * testRightValue;
		Double measuredValue = getRuntime().getSensorValue(testMultipliedDoubleSensor);

		String failMsg = testLeftValue + " * " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	// Tests for subclass DividedDoubleSensor:

	@Test
	public void testDividedDoubleSensorGetCheapValueCalculatesDivision() {
		DividedDoubleSensor testDividedDoubleSensor = new DividedDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = testLeftValue / testRightValue;
		Double measuredValue = testDividedDoubleSensor.getCheapValue();

		String failMsg = testLeftValue + " / " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	@Test
	public void testDividedDoubleSensorRoboticsRuntimeEvaluationCalculatesDivision()
			throws RoboticsException, InterruptedException {

		final DividedDoubleSensor testDividedDoubleSensor = new DividedDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = testLeftValue / testRightValue;
		Double measuredValue = getRuntime().getSensorValue(testDividedDoubleSensor);

		String failMsg = testLeftValue + " / " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	// Tests for subclass PowerDoubleSensor:

	@Test
	public void testPowerDoubleSensorGetCheapValueCalculatesPower() {
		PowerDoubleSensor testPowerDoubleSensor = new PowerDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = Math.pow(testLeftValue, testRightValue);
		Double measuredValue = testPowerDoubleSensor.getCheapValue();

		String failMsg = testLeftValue + " ^ " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	@Test
	public void testPowerDoubleSensorRoboticsRuntimeEvaluationCalculatesPower()
			throws RoboticsException, InterruptedException {

		PowerDoubleSensor testPowerDoubleSensor = new PowerDoubleSensor(leftSensor, rightSensor);

		Double expectedResult = Math.pow(testLeftValue, testRightValue);
		Double measuredValue = getRuntime().getSensorValue(testPowerDoubleSensor);

		String failMsg = testLeftValue + " ^ " + testRightValue + " is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	// Tests for subclass Atan2DoubleSensor:

	@Test
	public void testAtan2DoubleSensorGetCheapValueCalculatesAtan2() {
		Atan2DoubleSensor testAtan2DoubleSensor = new Atan2DoubleSensor(leftSensor, rightSensor);

		Double expectedResult = Math.atan2(testLeftValue, testRightValue);
		Double measuredValue = testAtan2DoubleSensor.getCheapValue();

		String failMsg = "atan2(" + testLeftValue + ", " + testRightValue + ") is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}

	@Test
	public void testAtan2DoubleSensorRoboticsRuntimeEvaluationCalculatesAtan2()
			throws RoboticsException, InterruptedException {

		Atan2DoubleSensor testAtan2DoubleSensor = new Atan2DoubleSensor(leftSensor, rightSensor);

		Double expectedResult = Math.atan2(testLeftValue, testRightValue);
		Double measuredValue = getRuntime().getSensorValue(testAtan2DoubleSensor);

		String failMsg = "atan2(" + testLeftValue + ", " + testRightValue + ") is";
		assertEquals(failMsg, expectedResult, measuredValue, 0.001);
	}
}
