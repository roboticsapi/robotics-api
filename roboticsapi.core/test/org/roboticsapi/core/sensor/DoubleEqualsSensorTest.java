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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.sensor.ConstantDoubleSensor;
import org.roboticsapi.core.sensor.DoubleEqualsSensor;

public class DoubleEqualsSensorTest {
	private DoubleEqualsSensor mockDoubleEqualsSensor = null;

	@Before
	public void setup() {
		mockDoubleEqualsSensor = new DoubleEqualsSensor(0.3, 0.2, 0.1);
	}

	@After
	public void teardown() {
		mockDoubleEqualsSensor = null;
	}

	@Test
	public void testConstructorWithThreeDoubleArgumentsExpectingNotNullSimpleTest() {
		assertNotNull(new DoubleEqualsSensor(0.3, 0.2, 0.1));
	}

	@Test
	public void testConstructorWithOneSensorAndTwoDoubleArgumentsExpectingNotNullSimpleTest() {
		assertNotNull(new DoubleEqualsSensor(new ConstantDoubleSensor(0.3), 0.2, 0.1));
	}

	@Test
	public void testGetEpsilonExpectingTestValueSimpleTest() {
		assertEquals(0.1, mockDoubleEqualsSensor.getEpsilon(), 0.001);
	}

	@Test
	public void testOverrideMethodToStringExpectingNotNullSimpleTest() {
		assertNotNull(mockDoubleEqualsSensor.toString());
	}

	@Test
	public void testInheritedMethodGetCheapValueWithTheTwoInnerSensorsHaveEqualValuesExpectingTrue() {
		DoubleEqualsSensor testDoubleEqualsSensor = new DoubleEqualsSensor(1.5, 1.5, 0.01);

		assertTrue(testDoubleEqualsSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueWithTheTwoInnerSensorsHaveDifferentValuesButWithinTheEpsilonExpectingTrue() {
		DoubleEqualsSensor testDoubleEqualsSensor = new DoubleEqualsSensor(1.5, 1.55, 0.1);

		assertTrue(testDoubleEqualsSensor.getCheapValue());
	}

	@Test
	public void testInheritedMethodGetCheapValueWithTheTwoInnerSensorsHaveDifferentValuesOutsideTheEpsilonExpectingFalse() {
		DoubleEqualsSensor testDoubleEqualsSensor = new DoubleEqualsSensor(1.5, 2.5, 0.1);

		assertFalse(testDoubleEqualsSensor.getCheapValue());
	}
}
