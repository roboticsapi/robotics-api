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
import org.roboticsapi.core.sensor.AbsIntegerSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.core.sensor.IntegerSensor;

public class AbsIntegerSensorTest {
	private AbsIntegerSensor mockSensor = null;
	private IntegerSensor mockInnerSensor = null;

	@Before
	public void setup() {
		mockInnerSensor = new IntegerFromJavaSensor(1);
		mockSensor = new AbsIntegerSensor(mockInnerSensor);
	}

	@After
	public void teardown() {
		mockSensor = null;
		mockInnerSensor = null;
	}

	@Test
	public void testGetInnerSensorSimpleTest() {
		assertSame(mockInnerSensor, mockSensor.getInnerSensor());
	}

	@Test
	public void testToStringSimpleTest() {
		assertNotNull(mockSensor.toString());
	}

	@Test
	public void testComputeCheapValueByUsingMethodGetCheapValueSimpleTests() {
		assertSame(1, mockSensor.getCheapValue());

		mockSensor = new AbsIntegerSensor(new IntegerFromJavaSensor(-2));

		assertSame(2, mockSensor.getCheapValue());
	}
}
