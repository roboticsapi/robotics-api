/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.ConstantBooleanSensor;
import org.roboticsapi.core.sensor.ConstantIntegerSensor;

// Parameterized tests for override method equals(...) of class Sensor<T> and its subclasses:
@RunWith(Parameterized.class)
public class SensorMethodIsAvailableParameterizedTest<S extends Sensor<?>> {
	private final boolean testAvailable;

	private final S testSensor;

	public SensorMethodIsAvailableParameterizedTest(Class<?> c, S sensor, boolean available) {
		testSensor = sensor;
		testAvailable = available;
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> testValues() {
		Class<?>[] classes = new Class<?>[] { ConstantBooleanSensor.class, ConstantIntegerSensor.class };

		final int sensorCount = classes.length;
		final int parameterCount = 3;

		Object[][] testValues = new Object[sensorCount][parameterCount];

		// ConstantBooleanSensor:
		testValues[0] = new Object[] { classes[0], new ConstantBooleanSensor(true), true };

		// ConstantIntegerSensor:
		testValues[1] = new Object[] { classes[1], new ConstantIntegerSensor(1), true };

		return Arrays.asList(testValues);
	}

	@Test
	public void testExpectingReturnedValueEqualsDefinedValue() {
		assertEquals(testAvailable, testSensor.isAvailable());
	}
}
