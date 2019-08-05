/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.IntegerConditionalSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;

public class IntegerConditionalSensorTest {
	private final int INT_IF_TRUE = 1;
	private final int INT_IF_FALSE = 0;

	private IntegerConditionalSensor mockSensor = null;

	@Test
	public void testConstructorWithBooleanSensorAndTwoIntegerSensors() {

		mockSensor = new IntegerConditionalSensor(new BooleanFromJavaSensor(false),
				new IntegerFromJavaSensor(INT_IF_TRUE), new IntegerFromJavaSensor(INT_IF_FALSE));

		assertFalse(mockSensor.getCondition().getCheapValue());
		assertSame(INT_IF_TRUE, mockSensor.getIfTrue().getCheapValue());
		assertSame(INT_IF_FALSE, mockSensor.getIfFalse().getCheapValue());
	}

	@Test
	public void testConstructorWithBooleanSensorIntValueForTrueAndIntegerSensorForFalse() {
		mockSensor = new IntegerConditionalSensor(new BooleanFromJavaSensor(true), INT_IF_TRUE,
				new IntegerFromJavaSensor(INT_IF_FALSE));

		assertTrue(mockSensor.getCondition().getCheapValue());
		assertSame(INT_IF_TRUE, mockSensor.getIfTrue().getCheapValue());
		assertSame(INT_IF_FALSE, mockSensor.getIfFalse().getCheapValue());
	}

	@Test
	public void testConstructorWithBooleanSensorIntegerSensorForTrueAndIntValueForFalse() {
		mockSensor = new IntegerConditionalSensor(new BooleanFromJavaSensor(false),
				new IntegerFromJavaSensor(INT_IF_TRUE), INT_IF_FALSE);

		assertFalse(mockSensor.getCondition().getCheapValue());
		assertSame(INT_IF_TRUE, mockSensor.getIfTrue().getCheapValue());
		assertSame(INT_IF_FALSE, mockSensor.getIfFalse().getCheapValue());
	}

	@Test
	public void testConstructorWithBooleanSensorAndTwoIntValues() {
		mockSensor = new IntegerConditionalSensor(new BooleanFromJavaSensor(true), INT_IF_TRUE, INT_IF_FALSE);

		assertTrue(mockSensor.getCondition().getCheapValue());
		assertSame(INT_IF_TRUE, mockSensor.getIfTrue().getCheapValue());
		assertSame(INT_IF_FALSE, mockSensor.getIfFalse().getCheapValue());
	}

	@Test
	public void testToStringSimpleTest() {
		mockSensor = new IntegerConditionalSensor(new BooleanFromJavaSensor(true), INT_IF_TRUE, INT_IF_FALSE);

		assertNotNull(mockSensor.toString());
	}

	@Test
	public void testComputeCheapValueByUsingMethodGetCheapValue() {
		mockSensor = new IntegerConditionalSensor(new BooleanFromJavaSensor(true), INT_IF_TRUE, INT_IF_FALSE);

		assertSame(INT_IF_TRUE, mockSensor.getCheapValue());

		mockSensor = new IntegerConditionalSensor(new BooleanFromJavaSensor(false), INT_IF_TRUE, INT_IF_FALSE);

		assertSame(INT_IF_FALSE, mockSensor.getCheapValue());
	}
}
