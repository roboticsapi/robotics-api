/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationSensorConnection;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.ConstantVelocitySensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public abstract class AbstractVelocitySensorTest extends AbstractRuntimeTest {

	private Frame r, m;
	private VelocitySensor s;

	@SuppressWarnings("unused")
	private Twist lastValue;
	@SuppressWarnings("unused")
	private int updateCount = 0;

	@Before
	public void init() throws RoboticsException {
		lastValue = null;
		updateCount = 0;
		r = new Frame("VelocitySensorTest r");
		getContext().initialize(r);
		m = new Frame("VelocitySensorTest m");
		getContext().initialize(m);

		ConstantVelocitySensor velSensor = new ConstantVelocitySensor(m, r, m.getPoint(), r.getOrientation(),
				new Twist(1, 2, 3, 1, 2, 3));
		r.addRelation(
				new TransformationSensorConnection(TransformationSensor.fromConstant(new Transformation()), velSensor),
				m);

		s = r.getVelocitySensorOf(m);
	}

	@After
	public void deinit() {
		r = m = null;
		s = null;
	}

	@Test(timeout = 2000)
	public void testConstantSensorReportsCorrectValuesFromRuntime() {
		try {
			Twist val = getCurrentSensorValue(s);

			Assert.assertEquals(1, val.getTransVel().getX(), 0.001);
			Assert.assertEquals(2, val.getTransVel().getY(), 0.001);
			Assert.assertEquals(3, val.getTransVel().getZ(), 0.001);
			Assert.assertEquals(1, val.getRotVel().getX(), 0.001);
			Assert.assertEquals(2, val.getRotVel().getY(), 0.001);
			Assert.assertEquals(3, val.getRotVel().getZ(), 0.001);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception:" + e);
		}
	}
}
