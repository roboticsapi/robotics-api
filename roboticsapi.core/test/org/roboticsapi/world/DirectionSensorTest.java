/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.World;
import org.roboticsapi.world.sensor.ConstantDirectionSensor;

public class DirectionSensorTest {

	@Test
	public void testConstantDirectionSensorFromDirectionDeliversConstantValue()
			throws SensorReadException, InitializationException {
		World world = new World();
		new RoboticsContextImpl("dummy").initialize(world);

		ConstantDirectionSensor sensor = new ConstantDirectionSensor(
				new Direction(world.getOrigin().getOrientation(), new Vector(1, 2, 3)));

		Direction currentValue = sensor.getCurrentValue();

		Assert.assertEquals(world.getOrigin().getOrientation().getReferenceFrame(),
				currentValue.getOrientation().getReferenceFrame());
		Assert.assertTrue(world.getOrigin().getOrientation().getRotation()
				.isEqualRotation(currentValue.getOrientation().getRotation()));
		Assert.assertTrue(currentValue.getValue().isEqualVector(new Vector(1, 2, 3)));
	}

	@Test
	public void testConstantDirectionSensorFromOrientationAndVectorDeliversConstantValue()
			throws SensorReadException, InitializationException {
		World world = new World();
		new RoboticsContextImpl("dummy").initialize(world);

		ConstantDirectionSensor sensor = new ConstantDirectionSensor(world.getOrigin().getOrientation(),
				new Vector(1, 2, 3));

		Direction currentValue = sensor.getCurrentValue();

		Assert.assertEquals(world.getOrigin().getOrientation().getReferenceFrame(),
				currentValue.getOrientation().getReferenceFrame());
		Assert.assertTrue(world.getOrigin().getOrientation().getRotation()
				.isEqualRotation(currentValue.getOrientation().getRotation()));

		Assert.assertTrue(currentValue.getValue().isEqualVector(new Vector(1, 2, 3)));
	}
}
