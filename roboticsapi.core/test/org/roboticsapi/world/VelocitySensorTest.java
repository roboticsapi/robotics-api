/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationSensorConnection;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.ConstantVelocitySensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class VelocitySensorTest {

	RoboticsContext context;

	public VelocitySensorTest() {
		context = new RoboticsContextImpl("dummy");
	}

	private Frame r, m;
	private VelocitySensor s;

	private Twist lastValue;
	private int updateCount = 0;

	@Before
	public void init() throws RoboticsException {
		// try {
		lastValue = null;
		updateCount = 0;
		r = new Frame("VelocitySensorTest r");
		m = new Frame("VelocitySensorTest m");

		ConstantVelocitySensor velSensor = new ConstantVelocitySensor(m, r, m.getPoint(), r.getOrientation(),
				new Twist(1, 2, 3, 1, 2, 3));
		context.initialize(r);
		r.addRelation(
				new TransformationSensorConnection(TransformationSensor.fromConstant(new Transformation()), velSensor),
				m);

		s = r.getVelocitySensorOf(m);
		// }
		// catch (FrameException e) {
		// Assert.fail("Unable to create frames or VelocitySensor");
		// }
	}

	@After
	public void deinit() {
		r = m = null;
		s = null;
	}

	@Test
	public void testConstantSensorReportsCorrectValues() {
		try {
			SensorListener<Twist> listener = new SensorListener<Twist>() {

				@Override
				public void onValueChanged(Twist newValue) {
					lastValue = newValue;
					updateCount++;
				}
			};
			s.addListener(listener);

			Thread.sleep(500);

			s.removeListener(listener);

			Assert.assertTrue(updateCount > 0);
			Assert.assertEquals(1, lastValue.getTransVel().getX(), 0.001);
			Assert.assertEquals(2, lastValue.getTransVel().getY(), 0.001);
			Assert.assertEquals(3, lastValue.getTransVel().getZ(), 0.001);
			Assert.assertEquals(1, lastValue.getRotVel().getX(), 0.001);
			Assert.assertEquals(2, lastValue.getRotVel().getY(), 0.001);
			Assert.assertEquals(3, lastValue.getRotVel().getZ(), 0.001);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception:" + e);
		}
	}

}
