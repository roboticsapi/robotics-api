/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleIsGreaterSensor;
import org.roboticsapi.core.sensor.SensorReadException;

@RunWith(Parameterized.class)
public abstract class AbstractDoubleIsGreaterSensorParameterizedTest extends AbstractRuntimeTest {
	private final double testLeftValue;
	private final double testRightValue;
	private final Boolean expectedResult;

	private DoubleIsGreaterSensor testSensor = null;
	private PersistContext<Double> testLeftPersist = null;
	private PersistContext<Double> testRightPersist = null;

	public AbstractDoubleIsGreaterSensorParameterizedTest(double leftVal, double rightVal, Boolean expected) {
		testLeftValue = leftVal;
		testRightValue = rightVal;
		expectedResult = expected;
	}

	@Parameterized.Parameters(name = "[{0}; {1}]; {2}")
	public static Collection<Object[]> testValues() {
		final double[] TEST_VALUES = new double[] { -42, 47.11, 0d, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN };

		final int len = TEST_VALUES.length;

		Object[][] testValues = new Object[len * len][3];

		Boolean result = null;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				result = (TEST_VALUES[i] > TEST_VALUES[j]) ? true : false;

				testValues[i * len + j] = new Object[] { TEST_VALUES[i], TEST_VALUES[j], result };
			}
		}

		return Arrays.asList(testValues);
	}

	@Before
	public void setup() throws RoboticsException {
		DoubleFromJavaSensor leftJavaSensor = new DoubleFromJavaSensor(testLeftValue);
		DoubleFromJavaSensor rightJavaSensor = new DoubleFromJavaSensor(testRightValue);

		testLeftPersist = leftJavaSensor.persist(getRuntime());
		testRightPersist = rightJavaSensor.persist(getRuntime());

		testSensor = new DoubleIsGreaterSensor(leftJavaSensor, rightJavaSensor);
	}

	@After
	public void teardown() throws CommandException {
		testLeftPersist.unpersist();
		testRightPersist.unpersist();

		testSensor = null;
	}

	@Test(timeout = 5000)
	public void testInheritedMethodGetCurrentValueOfSubclassDoubleIsGreaterSensorWithTestValuesExpectingCurrentSensorValueEqualsExpectedResult()
			throws SensorReadException {
		assertEquals(expectedResult, testSensor.getCurrentValue());
	}
}
