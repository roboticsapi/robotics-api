/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.mockclass.MockRealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeinteger.ConstantRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

// Parameterized tests for override method equals(...) of class Sensor<T> and its subclasses:
@RunWith(Parameterized.class)
public class SensorOverrideMethodsEqualsAndToStringParameterizedTest<S extends RealtimeValue<?>> {
	private final RealtimeValue<Object> testSuperSensor = new MockRealtimeValue<Object>(null);
	private final S testSensor;
	private final S testEqualSensor;
	private final S testDifferentSensor;

	public SensorOverrideMethodsEqualsAndToStringParameterizedTest(Class<?> c, S sensor, S eqSensor, S diffSensor) {
		testSensor = sensor;
		testEqualSensor = eqSensor;
		testDifferentSensor = diffSensor;
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> testValues() {
		Class<?>[] classes = new Class<?>[] { ConstantRealtimeBoolean.class, ConstantRealtimeInteger.class,
				OrRealtimeBoolean.class };

		final int sensorCount = classes.length;
		final int parameterCount = 4;

		Object[][] testValues = new Object[sensorCount][parameterCount];

		// ConstantBooleanSensor:
		testValues[0] = new Object[] { classes[0], new ConstantRealtimeBoolean(true), new ConstantRealtimeBoolean(true),
				new ConstantRealtimeBoolean(false) };

		// ConstantIntegerSensor:
		testValues[1] = new Object[] { classes[1], RealtimeInteger.createFromConstant(1),
				RealtimeInteger.createFromConstant(1), RealtimeInteger.createFromConstant(2) };

		// OrBooleanSensor:
		RealtimeBoolean trueSensor1 = new ConstantRealtimeBoolean(true);
		RealtimeBoolean trueSensor2 = new ConstantRealtimeBoolean(true);
		RealtimeBoolean falseSensor = new ConstantRealtimeBoolean(false);
		testValues[2] = new Object[] { classes[2], new OrRealtimeBoolean(trueSensor1, trueSensor2),
				new OrRealtimeBoolean(trueSensor1, trueSensor2), new OrRealtimeBoolean(trueSensor1, falseSensor) };

		return Arrays.asList(testValues);
	}

	@Test
	public void testOverrideMethodEqualsWithTwoEqualSensorsExpectingTrue() {
		assertTrue(testSensor.equals(testEqualSensor));
		assertTrue(testEqualSensor.equals(testSensor));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoSensorsWithDifferentValuesExpectingFalse() {
		assertFalse(testSensor.equals(testDifferentSensor));
		assertFalse(testDifferentSensor.equals(testSensor));
	}

	@Test
	public void testOverrideMethodEqualsWithTwoDifferentSensorsExpectingFalse() {
		assertFalse(testSensor.equals(testSuperSensor));
	}

	@Test
	public void testOverrideMethodToStringExpectingNotNull() {
		assertNotNull(testSensor.toString());
	}
}
