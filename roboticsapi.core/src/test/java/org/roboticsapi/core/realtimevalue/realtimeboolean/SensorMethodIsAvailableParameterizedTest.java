/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeinteger.ConstantRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

// Parameterized tests for override method equals(...) of class Sensor<T> and its subclasses:
@RunWith(Parameterized.class)
public class SensorMethodIsAvailableParameterizedTest<S extends RealtimeValue<?>> {
	private final boolean testAvailable;

	private final S testSensor;

	public SensorMethodIsAvailableParameterizedTest(Class<?> c, S sensor, boolean available) {
		testSensor = sensor;
		testAvailable = available;
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> testValues() {
		Class<?>[] classes = new Class<?>[] { ConstantRealtimeBoolean.class, ConstantRealtimeInteger.class };

		final int sensorCount = classes.length;
		final int parameterCount = 3;

		Object[][] testValues = new Object[sensorCount][parameterCount];

		// ConstantBooleanSensor:
		testValues[0] = new Object[] { classes[0], new ConstantRealtimeBoolean(true), true };

		// ConstantIntegerSensor:
		testValues[1] = new Object[] { classes[1], RealtimeInteger.createFromConstant(1), true };

		return Arrays.asList(testValues);
	}

	@Test
	public void testExpectingReturnedValueEqualsDefinedValue() {
		assertEquals(testAvailable, testSensor.isAvailable());
	}
}
