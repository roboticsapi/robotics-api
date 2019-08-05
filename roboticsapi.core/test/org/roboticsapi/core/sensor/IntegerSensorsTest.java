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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.sensor.AddedIntegerSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.core.sensor.IntegerSensor;
import org.roboticsapi.core.sensor.MultipliedIntegerSensor;
import org.roboticsapi.core.sensor.NegatedIntegerSensor;
import org.roboticsapi.mockclass.MockIntegerSensor;

// Tests for class IntegerSensor its subclasses *Integer*Sensor:
public class IntegerSensorsTest {
	private IntegerFromJavaSensor mockSensor = null;

	// for test method testGetDefaultValueOfSuperclassIntegerSensorSimpleTest():
	private class SpecialMockIntegerSensor extends MockIntegerSensor {
		public SpecialMockIntegerSensor(RoboticsRuntime runtime) {
			super(runtime);
		}

		@Override
		public Integer getDefaultValue() {
			return super.getDefaultValue();
		}
	}

	@Before
	public void setup() {
		mockSensor = new IntegerFromJavaSensor(0);
	}

	@After
	public void teardown() {
		mockSensor = null;
	}

	// Tests for class IntegerSensor:

	@Test
	public void testNegateOfSuperclassIntegerSensorSimpleTests() {
		IntegerSensor testNegSensor = mockSensor.negate();

		assertSame(0, testNegSensor.getCheapValue());

		mockSensor.setValue(10);

		assertSame(-10, testNegSensor.getCheapValue());

		mockSensor.setValue(-20);

		assertSame(20, testNegSensor.getCheapValue());
	}

	@Test
	public void testAddOfSuperclassIntegerSensorWithIntegerSensorArgumentSimpleTests() {
		IntegerSensor testSensor = new IntegerFromJavaSensor(10);

		IntegerSensor testAddedSensor = mockSensor.add(testSensor);

		assertSame(10, testAddedSensor.getCheapValue());

		mockSensor.setValue(10);

		assertSame(20, testAddedSensor.getCheapValue());
	}

	@Test
	public void testAddOfSuperclassIntegerSensorWithIntArgumentSimpleTests() {
		IntegerSensor testAddedSensor = mockSensor.add(20);

		assertSame(20, testAddedSensor.getCheapValue());

		mockSensor.setValue(10);

		assertSame(30, testAddedSensor.getCheapValue());
	}

	@Test
	public void testMinusOfSuperclassIntegerSensorWithIntegerSensorArgumentSimpleTests() {
		IntegerSensor testSensor = new IntegerFromJavaSensor(10);

		IntegerSensor testMinusSensor = mockSensor.minus(testSensor);

		assertSame(-10, testMinusSensor.getCheapValue());

		mockSensor.setValue(30);

		assertSame(20, testMinusSensor.getCheapValue());
	}

	@Test
	public void testMinusOfSuperclassIntegerSensorWithIntArgumentSimpleTests() {
		IntegerSensor testMinusSensor = mockSensor.minus(10);

		assertSame(-10, testMinusSensor.getCheapValue());

		mockSensor.setValue(30);

		assertSame(20, testMinusSensor.getCheapValue());
	}

	@Test
	public void testMultiplyOfSuperclassIntegerSensorWithIntegerSensorArgumentSimpleTests() {
		IntegerSensor testSensor = new IntegerFromJavaSensor(5);

		mockSensor.setValue(4);

		IntegerSensor testMultipliedSensor = mockSensor.multiply(testSensor);

		assertSame(20, testMultipliedSensor.getCheapValue());

		mockSensor.setValue(11);

		assertSame(55, testMultipliedSensor.getCheapValue());
	}

	@Test
	public void testMultiplyOfSuperclassIntegerSensorWithIntArgumentSimpleTests() {
		mockSensor.setValue(3);

		IntegerSensor testMultipliedSensor = mockSensor.multiply(5);

		assertSame(15, testMultipliedSensor.getCheapValue());

		mockSensor.setValue(5);

		assertSame(25, testMultipliedSensor.getCheapValue());
	}

	@Test
	public void testSquareOfSuperclassIntegerSensorSimpleTests() {
		IntegerSensor testSquareSensor = mockSensor.square();

		mockSensor.setValue(5);

		assertSame(25, testSquareSensor.getCheapValue());

		mockSensor.setValue(8);

		assertSame(64, testSquareSensor.getCheapValue());
	}

	@Test
	public void testGetDefaultValueOfSuperclassIntegerSensorSimpleTest() {
		SpecialMockIntegerSensor testSensor = new SpecialMockIntegerSensor(null);

		assertSame(0, testSensor.getDefaultValue());
	}

	@Test
	public void testFromValueWithIntArgumentSimpleTest() {
		IntegerSensor testSensor = IntegerSensor.fromValue(12);

		assertSame(12, testSensor.getCheapValue());
	}

	// Tests for subclass IntegerFromJavaSensor:

	@Test
	public void testGetDefaultValueOfSubclassIntegerFromJavaSensorSimpleTest() {
		assertSame(0, mockSensor.getDefaultValue());
	}

	@Test
	public void testIsAvailableOfSubclassIntegerFromJavaSensorSimpleTest() {
		assertTrue(mockSensor.isAvailable());
	}

	@Test
	public void testToStringOfSubclassIntegerFromJavaSensorSimpleTest() {
		assertNotNull(mockSensor.toString());
	}

	// Tests for subclass AddedIntegerSensor:

	@Test
	public void testMethodGetAddend1OfSubclassAddedIntegerSensorWithTestSensorExpectingTheTestSensorSimpleTest() {
		IntegerSensor testIntegerSensor1 = new IntegerFromJavaSensor(1);

		AddedIntegerSensor testAddedIntegerSensor = new AddedIntegerSensor(testIntegerSensor1,
				new IntegerFromJavaSensor(2));

		assertNotNull(testAddedIntegerSensor.getAddend1());

		assertEquals(testIntegerSensor1, testAddedIntegerSensor.getAddend1());
	}

	@Test
	public void testMethodGetAddend2OfSubclassAddedIntegerSensorWithTestSensorExpectingTheTestSensorSimpleTest() {
		IntegerSensor testIntegerSensor2 = new IntegerFromJavaSensor(2);

		AddedIntegerSensor testAddedIntegerSensor = new AddedIntegerSensor(new IntegerFromJavaSensor(1),
				testIntegerSensor2);

		assertNotNull(testAddedIntegerSensor.getAddend2());

		assertEquals(testIntegerSensor2, testAddedIntegerSensor.getAddend2());
	}

	@Test
	public void testOverrideMethodToStringOfSubclassAddedIntegerSensorExpectingNeitherNullNorEmptySimpleTest() {
		AddedIntegerSensor testAddedIntegerSensor = new AddedIntegerSensor(new IntegerFromJavaSensor(0),
				new IntegerFromJavaSensor(1));

		assertNotNull(testAddedIntegerSensor.toString());

		assertFalse(testAddedIntegerSensor.toString().isEmpty());
	}

	// Tests for subclass MultipliedIntegerSensor:

	@Test
	public void testGetMultiplicandExpectingEqualsTestInnerSensor() {
		IntegerSensor testInnerSensor1 = new IntegerFromJavaSensor(1);
		IntegerSensor testInnerSensor2 = new IntegerFromJavaSensor(2);

		MultipliedIntegerSensor testSensor = new MultipliedIntegerSensor(testInnerSensor1, testInnerSensor2);

		assertEquals(testInnerSensor1, testSensor.getMultiplicand());
	}

	@Test
	public void testGetMultiplierExpectingEqualsTestInnerSensor() {
		IntegerSensor testInnerSensor1 = new IntegerFromJavaSensor(1);
		IntegerSensor testInnerSensor2 = new IntegerFromJavaSensor(2);

		MultipliedIntegerSensor testSensor = new MultipliedIntegerSensor(testInnerSensor1, testInnerSensor2);

		assertEquals(testInnerSensor2, testSensor.getMultiplier());
	}

	@Test
	public void testOverrideMethodToStringExpectingNotNull() {
		IntegerSensor testInnerSensor1 = new IntegerFromJavaSensor(1);
		IntegerSensor testInnerSensor2 = new IntegerFromJavaSensor(2);

		MultipliedIntegerSensor testSensor = new MultipliedIntegerSensor(testInnerSensor1, testInnerSensor2);

		assertNotNull(testSensor.toString());
	}

	// Tests for class NegatedIntegerSensor:

	@Test
	public void testGetInnerSensorOfClassNegatedIntegerSensorWithNonnullInnerSensorExpectingReturnedSensorEqualsTestInnerSensor() {
		IntegerSensor innerSensor = new IntegerFromJavaSensor(1);
		NegatedIntegerSensor testSensor = new NegatedIntegerSensor(innerSensor);

		assertEquals(innerSensor, testSensor.getInnerSensor());
	}

	@Test
	public void testOverrideMethodToStringOfClassNegatedIntegerSensorExpectingNotNull() {
		IntegerSensor innerSensor = new IntegerFromJavaSensor(1);
		NegatedIntegerSensor testSensor = new NegatedIntegerSensor(innerSensor);

		assertNotNull(testSensor.toString());
	}
}
