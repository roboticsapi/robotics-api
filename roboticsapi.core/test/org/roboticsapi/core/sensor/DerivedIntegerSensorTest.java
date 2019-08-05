/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.sensor.DerivedIntegerSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.core.sensor.IntegerSensor;
import org.roboticsapi.mockclass.MockDerivedIntegerSensor;
import org.roboticsapi.mockclass.MockIntegerSensor;

public class DerivedIntegerSensorTest {
	private DerivedIntegerSensor mockSensor = null;

	@Before
	public void setup() {
		mockSensor = new MockDerivedIntegerSensor(new IntegerFromJavaSensor(0));
	}

	@After
	public void teardown() {
		mockSensor = null;
	}

	@Test
	public void testIsAvailableSimpleTest() {
		assertTrue(mockSensor.isAvailable());
	}

	@Test
	public void testCalculateCheapValueByUsingMethodGetCheapValueSimpleTest() {
		assertSame(0, mockSensor.getCheapValue());
	}

	@Test
	public void testCalculateCheapValueByUsingMethodGetCheapValueWithTestSensorWithNullCheapValue() {
		mockSensor = new MockDerivedIntegerSensor(new MockIntegerSensor(null));

		assertNull(mockSensor.getCheapValue());
	}

	@Test
	public void testEqualsWithInvalidCompareObjectExpectingFalse() {
		assertFalse(mockSensor.equals(new Object()));
	}

	@Test
	public void testEqualsWithAnotherDerivedIntegerSensorButInvalidInnerSensorExpectingFalse() {
		DerivedIntegerSensor otherSensor = new MockDerivedIntegerSensor(new MockIntegerSensor(null));

		assertFalse(mockSensor.equals(otherSensor));
	}

	@Test
	public void testEqualsWithAnotherDerivedIntegerSensorWithSameInnerSensorExpectingTrue() {
		IntegerSensor testInnerSensor = new IntegerFromJavaSensor(0);
		mockSensor = new MockDerivedIntegerSensor(testInnerSensor);
		DerivedIntegerSensor otherSensor = new MockDerivedIntegerSensor(testInnerSensor);

		assertTrue(mockSensor.equals(otherSensor));
	}

	@Test
	public void testHashCodeSimplyByCalling() {
		mockSensor.hashCode();
	}
}
