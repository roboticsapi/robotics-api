/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleConditionalSensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;

public abstract class AbstractDoubleConditionalSensorTest extends AbstractRuntimeTest {
	@Test(timeout = 10000)
	public void testInheritedMethodGetCurrentValueWithTestValuesAndMeasuringValuesExpectingEqualityOfTestValuesAndMeasuredValues()
			throws InterruptedException, RoboticsException {
		boolean flag = false;

		final double[] VALUES_IF_TRUE = new double[] { 5.3, 6.5, 7.8, 8.0, 9.1 };
		final double[] VALUES_IF_FALSE = new double[] { -0.7, -1.5, -2.2, -3.0, 4.9 };

		assertTrue("The test value array have different lengths. Test will be aborted.",
				VALUES_IF_TRUE.length == VALUES_IF_FALSE.length);

		final int VALUES_COUNT = VALUES_IF_TRUE.length;

		BooleanFromJavaSensor testBooleanFromJavaSensor = new BooleanFromJavaSensor(flag);
		PersistContext<Boolean> testBooleanPersistContext = testBooleanFromJavaSensor.persist(getRuntime());
		DoubleFromJavaSensor testDoubleSensorIfTrue = new DoubleFromJavaSensor(VALUES_IF_TRUE[0]);
		DoubleFromJavaSensor testDoubleSensorIfFalse = new DoubleFromJavaSensor(VALUES_IF_FALSE[0]);

		DoubleConditionalSensor mockDoubleConditionalSensor = new DoubleConditionalSensor(
				testBooleanPersistContext.getSensor(), testDoubleSensorIfTrue, testDoubleSensorIfFalse);

		// array for the expected values:
		double[] expectedValues = new double[VALUES_COUNT];

		// array for the measured values:
		double[] measuredValues = new double[VALUES_COUNT];

		// measure procedure:
		for (int i = 0; i < VALUES_COUNT; i++) {
			// switch flag
			flag = !flag;

			expectedValues[i] = flag ? VALUES_IF_TRUE[i] : VALUES_IF_FALSE[i];

			testBooleanFromJavaSensor.setValue(flag);
			testDoubleSensorIfTrue.setValue(VALUES_IF_TRUE[i]);
			testDoubleSensorIfFalse.setValue(VALUES_IF_FALSE[i]);
			Thread.sleep(50);

			measuredValues[i] = mockDoubleConditionalSensor.getCurrentValue();

			Thread.sleep(100);
		}

		Thread.sleep(1000);

		testBooleanPersistContext.unpersist();

		// proper test:
		for (int i = 0; i < VALUES_COUNT; i++) {
			assertEquals(expectedValues[i], measuredValues[i], 0.001);
		}
	}
}
