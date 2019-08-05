/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.sensor.DividedIntegerSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;

public class DividedIntegerSensorTest {
	private DividedIntegerSensor mockSensor = null;
	private IntegerFromJavaSensor mockDividend = null;
	private IntegerFromJavaSensor mockDivisor = null;

	@Before
	public void setup() {
		mockDividend = new IntegerFromJavaSensor(2);
		mockDivisor = new IntegerFromJavaSensor(1);
		mockSensor = new DividedIntegerSensor(mockDividend, mockDivisor);
	}

	@After
	public void teardown() {
		mockSensor = null;
		mockDividend = null;
		mockDivisor = null;
	}

	@Test
	public void testGetDividendSimpleTest() {
		assertSame(mockDividend, mockSensor.getDividend());
	}

	@Test
	public void testGetDivisorSimpleTest() {
		assertSame(mockDivisor, mockSensor.getDivisor());
	}

	@Test
	public void testToStringSimpleTest() {
		assertNotNull(mockSensor.toString());
	}

	@Test
	public void testComputeCheapValueByUsingMethodGetCheapValueWithValidValues() {
		assertSame(2, mockSensor.getCheapValue());

		mockDividend.setValue(3);
		mockDivisor.setValue(2);

		assertSame(1, mockSensor.getCheapValue());

		mockDividend.setValue(-3);

		assertSame(-1, mockSensor.getCheapValue());
	}

	@Test(expected = ArithmeticException.class)
	public void testComputeCheapValueByUsingMethodGetCheapValueWithZeroAsInvalidDivisorExpectingException() {
		mockDivisor.setValue(0);

		mockSensor.getCheapValue();
	}
}
