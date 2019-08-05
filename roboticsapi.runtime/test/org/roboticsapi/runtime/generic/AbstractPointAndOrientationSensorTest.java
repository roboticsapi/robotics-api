/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.PointSensor;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VectorSensor;

public abstract class AbstractPointAndOrientationSensorTest extends AbstractRuntimeTest {

	@Test
	public void testVectorSensorGetCurrentPointGetsPointWithRightReferenceAndDisplacement() throws SensorReadException {
		Frame origin = new Frame("Origin");

		PointSensor fromConstant = PointSensor.fromConstant(origin, new Vector(1, 2.2, 3.3));

		Point point = fromConstant.getCurrentValue();

		Assert.assertTrue(point.getReferenceFrame().equals(origin));
		Assert.assertTrue(point.getVector().isEqualVector(new Vector(1, 2.2, 3.3)));
	}

	@Test
	public void testVectorFromConstantReportsConstantValue() throws SensorReadException {
		Frame origin = new Frame("Origin");

		PointSensor fromConstant = PointSensor.fromConstant(origin, new Vector(1, 2.2, 3.3));

		Assert.assertTrue(fromConstant.getCurrentValue().getVector().isEqualVector(new Vector(1, 2.2, 3.3)));
	}

	@Test
	public void testVectorFromComponentsSensorReportsSensorValues() throws SensorReadException, InterruptedException {
		Frame origin = new Frame("Origin");

		DoubleFromJavaSensor xSensor = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor ySensor = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor zSensor = new DoubleFromJavaSensor(0);

		PointSensor sensor = PointSensor.fromComponents(origin, xSensor, ySensor, zSensor);

		Assert.assertTrue(new Vector().isEqualVector(sensor.getCurrentValue().getVector()));

		xSensor.setValue(0.3);

		Assert.assertTrue(new Vector(0.3, 0, 0).isEqualVector(sensor.getCurrentValue().getVector()));

		xSensor.setValue(0.4);
		ySensor.setValue(0.8);
		zSensor.setValue(1.4);

		Vector currentValue = sensor.getCurrentValue().getVector();
		Assert.assertTrue(new Vector(0.4, 0.8, 1.4).isEqualVector(currentValue));
	}

	@Test
	public void testReferenceFrameAdaptationReportsCorrectValues() throws SensorReadException, RoboticsException {
		Frame origin = new Frame("Origin");

		Frame f1 = origin.plus(1, 1, 1, 2, 2, 2);
		Frame f2 = f1.plus(0.5, 2, 1, 0.5, 0.2, 0.7);
		Frame f3 = f2.plus(0, 1, 7, 0.2, 0.3, 0.4);

		PointSensor sensor = PointSensor.fromConstant(origin, new Vector());

		Assert.assertTrue(f1.getTransformationTo(origin).getTranslation()
				.isEqualVector(sensor.changeReference(f1).getCurrentValue().getVector()));

		Assert.assertTrue(f2.getTransformationTo(origin).getTranslation()
				.isEqualVector(sensor.changeReference(f2).getCurrentValue().getVector()));

		Assert.assertTrue(f3.getTransformationTo(origin).getTranslation()
				.isEqualVector(sensor.changeReference(f3).getCurrentValue().getVector()));
	}

	private boolean equals = false;

	@Test
	public void testTranslationFromTransformationSensorReportsTranslationValue()
			throws RoboticsException, InterruptedException {
		Frame origin = new Frame("Origin");

		Frame f1 = origin.plus(1, 1, 1, 2, 2, 2);
		Frame f2 = f1.plus(0.5, 2, 1, 0.5, 0.2, 0.7);
		Frame f3 = f2.plus(0, 1, 7, 0.2, 0.3, 0.4);

		final TransformationSensor ts = origin.getRelationSensor(f3).getTransformationSensor();
		final Semaphore sem = new Semaphore(1);

		sem.acquire();

		ts.getTranslationSensor().addListener(new SensorListener<Vector>() {

			@Override
			public void onValueChanged(Vector newValue) {
				try {

					VectorSensor translationSensor = ts.getTranslationSensor();

					Vector vec = new Vector(translationSensor.getXSensor().getCurrentValue(),
							translationSensor.getYSensor().getCurrentValue(),
							translationSensor.getZSensor().getCurrentValue());

					System.out.println(vec);

					equals = newValue.isEqualVector(vec);
				} catch (SensorReadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sem.release();
			}
		});

		sem.acquire();

		Assert.assertTrue(equals);
	}

	@Test
	public void testRotationFromTransformationSensorReportsTranslationValue()
			throws RoboticsException, InterruptedException {
		Frame origin = new Frame("Origin");

		Frame f1 = origin.plus(1, 1, 1, 2, 2, 2);
		Frame f2 = f1.plus(0.5, 2, 1, 0.5, 0.2, 0.7);
		Frame f3 = f2.plus(0, 1, 7, 0.2, 0.3, 0.4);

		final RelationSensor ts = origin.getRelationSensor(f3);

		equals = false;
		final Semaphore sem = new Semaphore(1);

		sem.acquire();

		ts.getOrientationSensor().addListener(new SensorListener<Orientation>() {

			@Override
			public void onValueChanged(Orientation newValue) {
				try {
					Orientation or = new Orientation(ts.getFrom(),
							new Rotation(ts.getOrientationSensor().getASensor().getCurrentValue(),
									ts.getOrientationSensor().getBSensor().getCurrentValue(),
									ts.getOrientationSensor().getCSensor().getCurrentValue()));

					equals = newValue.isEqualOrientation(or);
				} catch (SensorReadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sem.release();
			}
		});

		sem.acquire();

		Assert.assertTrue(equals);
	}
}
