/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.QueueSensorListener;

public abstract class AbstractQueueSensorListenerTest extends AbstractRuntimeTest {
	private class MockDoubleQueueSensorListener extends QueueSensorListener<Double> {
		@Override
		protected void valueChanged(Double newValue) {
			measuredValues.add(newValue);
		}
	}

	@Test(timeout = 1000)
	public void testConstructorExpectingNotNull() {
		QueueSensorListener<Double> testQueueSensorListenerDouble = new MockDoubleQueueSensorListener();

		assertNotNull(testQueueSensorListenerDouble);
	}

	private final List<Double> measuredValues = new ArrayList<Double>();

	@Test(timeout = 10000)
	public void testQueueSensorListenerDoubleObjectWithDoubleSensorExpectingTestValuesEqualsSetValues()
			throws RoboticsException, InterruptedException {
		DoubleFromJavaSensor testDoubleFromJavaSensor = new DoubleFromJavaSensor(0);

		PersistContext<Double> testDoublePersistContext = testDoubleFromJavaSensor.persist(getRuntime());

		QueueSensorListener<Double> testQueueSensorListener = new MockDoubleQueueSensorListener();

		testDoublePersistContext.getSensor().addListener(testQueueSensorListener);

		Double[] testValues = new Double[] { 3.4, -9.2, 0.7 };

		for (int i = 0; i < testValues.length; i++) {
			testDoubleFromJavaSensor.setValue(testValues[i]);

			Thread.sleep(1000);
		}

		Thread.sleep(1000);

		testDoublePersistContext.unpersist();

		// TODO: This solution is not clean
		int j = 0;
		for (Double mv : measuredValues) {
			if (mv.equals(testValues[j])) {
				j++;
			} else if (!mv.equals(0d)) {
				fail("At least one of the measured values does is not equal its test value.");
			}
		}
	}
}
