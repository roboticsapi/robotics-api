/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.runtime.mockclass.MockTransformationSensor;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.TransformationSensor;

public abstract class AbstractTransformationSensorTest extends AbstractRuntimeTest {
	private class SpecialMockTransformationSensor extends MockTransformationSensor {
		public SpecialMockTransformationSensor(RoboticsRuntime runtime) {
			super(runtime);
		}

		@Override
		public final Transformation getDefaultValue() {
			return super.getDefaultValue();
		}
	}

	@Test(timeout = 3000)
	public void testTransformationFromComponentsSensorDeliversValuesOfConstantChildren() throws SensorReadException {
		TransformationSensor sensor = TransformationSensor.fromComponents(DoubleSensor.fromValue(1),
				DoubleSensor.fromValue(2), DoubleSensor.fromValue(3), DoubleSensor.fromValue(3),
				DoubleSensor.fromValue(2), DoubleSensor.fromValue(1));

		Transformation currentValue = sensor.getCurrentValue();

		Assert.assertNotNull(currentValue);

		Assert.assertTrue(new Transformation(1, 2, 3, 3, 2, 1).isEqualTransformation(currentValue));
	}

	@Test(timeout = 3000)
	public void testTransformationFromComponentsSensorDeliversValuesOfDynamicChildren() throws SensorReadException {
		DoubleFromJavaSensor x = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor y = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor z = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor a = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor b = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor c = new DoubleFromJavaSensor(0);

		TransformationSensor sensor = TransformationSensor.fromComponents(x, y, z, a, b, c);

		Transformation currentValue = sensor.getCurrentValue();

		Assert.assertNotNull(currentValue);

		Assert.assertTrue(new Transformation(0, 0, 0, 0, 0, 0).isEqualTransformation(currentValue));

		x.setValue(1);
		y.setValue(2);
		z.setValue(3);
		a.setValue(3);
		b.setValue(2);
		c.setValue(1);

		currentValue = sensor.getCurrentValue();

		Assert.assertNotNull(currentValue);

		Assert.assertTrue(new Transformation(1, 2, 3, 3, 2, 1).isEqualTransformation(currentValue));
	}

	@Test(timeout = 3000)
	public void testOverrideMethodGetDefaultValueExpectingNotNullSimpleTest() {
		SpecialMockTransformationSensor testSensor = new SpecialMockTransformationSensor(getRuntime());

		assertNotNull(testSensor.getDefaultValue());
	}
}
