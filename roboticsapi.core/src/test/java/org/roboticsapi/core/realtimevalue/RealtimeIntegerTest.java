/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.WritableRealtimeInteger;

// Tests for class RealtimeInteger its subclasses *Integer*Sensor:
public class RealtimeIntegerTest {
	private WritableRealtimeInteger mockSensor = null;

	// for test method
	// testGetDefaultValueOfSuperclassRealtimeIntegerSimpleTest():
	private class SpecialMockRealtimeInteger extends MockRealtimeInteger {
		public SpecialMockRealtimeInteger(RoboticsRuntime runtime) {
			super(runtime);
		}
	}

	@Before
	public void setup() {
		mockSensor = RealtimeInteger.createWritable(0);
	}

	@After
	public void teardown() {
		mockSensor = null;
	}

	// Tests for class RealtimeInteger:

	@Test
	public void testNegateOfSuperclassRealtimeIntegerSimpleTests() {
		RealtimeInteger testNegSensor = mockSensor.negate();

		assertSame(0, testNegSensor.getCheapValue());

		mockSensor.setValue(10);

		assertSame(-10, testNegSensor.getCheapValue());

		mockSensor.setValue(-20);

		assertSame(20, testNegSensor.getCheapValue());
	}

	@Test
	public void testAddOfSuperclassRealtimeIntegerWithRealtimeIntegerArgumentSimpleTests() {
		RealtimeInteger testSensor = RealtimeInteger.createWritable(10);

		RealtimeInteger testAddedSensor = mockSensor.add(testSensor);

		assertSame(10, testAddedSensor.getCheapValue());

		mockSensor.setValue(10);

		assertSame(20, testAddedSensor.getCheapValue());
	}

	@Test
	public void testAddOfSuperclassRealtimeIntegerWithIntArgumentSimpleTests() {
		RealtimeInteger testAddedSensor = mockSensor.add(20);

		assertSame(20, testAddedSensor.getCheapValue());

		mockSensor.setValue(10);

		assertSame(30, testAddedSensor.getCheapValue());
	}

	@Test
	public void testMinusOfSuperclassRealtimeIntegerWithRealtimeIntegerArgumentSimpleTests() {
		RealtimeInteger testSensor = RealtimeInteger.createWritable(10);

		RealtimeInteger testMinusSensor = mockSensor.minus(testSensor);

		assertSame(-10, testMinusSensor.getCheapValue());

		mockSensor.setValue(30);

		assertSame(20, testMinusSensor.getCheapValue());
	}

	@Test
	public void testMinusOfSuperclassRealtimeIntegerWithIntArgumentSimpleTests() {
		RealtimeInteger testMinusSensor = mockSensor.minus(10);

		assertSame(-10, testMinusSensor.getCheapValue());

		mockSensor.setValue(30);

		assertSame(20, testMinusSensor.getCheapValue());
	}

	@Test
	public void testMultiplyOfSuperclassRealtimeIntegerWithRealtimeIntegerArgumentSimpleTests() {
		RealtimeInteger testSensor = RealtimeInteger.createWritable(5);

		mockSensor.setValue(4);

		RealtimeInteger testMultipliedSensor = mockSensor.multiply(testSensor);

		assertSame(20, testMultipliedSensor.getCheapValue());

		mockSensor.setValue(11);

		assertSame(55, testMultipliedSensor.getCheapValue());
	}

	@Test
	public void testMultiplyOfSuperclassRealtimeIntegerWithIntArgumentSimpleTests() {
		mockSensor.setValue(3);

		RealtimeInteger testMultipliedSensor = mockSensor.multiply(5);

		assertSame(15, testMultipliedSensor.getCheapValue());

		mockSensor.setValue(5);

		assertSame(25, testMultipliedSensor.getCheapValue());
	}

	@Test
	public void testSquareOfSuperclassRealtimeIntegerSimpleTests() {
		RealtimeInteger testSquareSensor = mockSensor.square();

		mockSensor.setValue(5);

		assertSame(25, testSquareSensor.getCheapValue());

		mockSensor.setValue(8);

		assertSame(64, testSquareSensor.getCheapValue());
	}

	@Test
	public void testFromValueWithIntArgumentSimpleTest() {
		RealtimeInteger testSensor = RealtimeInteger.createFromConstant(12);

		assertSame(12, testSensor.getCheapValue());
	}

}
