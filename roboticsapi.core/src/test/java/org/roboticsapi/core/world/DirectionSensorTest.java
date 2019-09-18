/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.world.realtimevalue.RealtimeDirection;

public class DirectionSensorTest {

	@Test
	public void testConstantDirectionSensorFromDirectionDeliversConstantValue()
			throws RealtimeValueReadException, InitializationException {
		World world = new World();
		new RoboticsContextImpl("dummy").initialize(world);

		RealtimeDirection sensor = RealtimeDirection
				.createFromConstant(new Direction(world.getOrigin().asOrientation(), new Vector(1, 2, 3)));

		Direction currentValue = sensor.getCurrentValue();

		Assert.assertEquals(world.getOrigin().asOrientation().getReferenceFrame(),
				currentValue.getOrientation().getReferenceFrame());
		Assert.assertTrue(world.getOrigin().asOrientation().getRotation()
				.isEqualRotation(currentValue.getOrientation().getRotation()));
		Assert.assertTrue(currentValue.getValue().isEqualVector(new Vector(1, 2, 3)));
	}

	@Test
	public void testConstantDirectionSensorFromOrientationAndVectorDeliversConstantValue()
			throws RealtimeValueReadException, InitializationException {
		World world = new World();
		new RoboticsContextImpl("dummy").initialize(world);

		RealtimeDirection sensor = RealtimeDirection
				.createFromConstant(new Direction(world.getOrigin().asOrientation(), new Vector(1, 2, 3)));

		Direction currentValue = sensor.getCurrentValue();

		Assert.assertEquals(world.getOrigin().asOrientation().getReferenceFrame(),
				currentValue.getOrientation().getReferenceFrame());
		Assert.assertTrue(world.getOrigin().asOrientation().getRotation()
				.isEqualRotation(currentValue.getOrientation().getRotation()));

		Assert.assertTrue(currentValue.getValue().isEqualVector(new Vector(1, 2, 3)));
	}
}
