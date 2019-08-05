/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.mockclass.MockTransformationSensor;
import org.roboticsapi.mockclass.MockVelocitySensor;
import org.roboticsapi.mockclass.TestRuntime;
import org.roboticsapi.world.MeasuredConnection;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class MeasuredConnectionTest {
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorOnThrowingExceptionWhenMeasuredRelationSensorIsNullExpectingIllegalArgumentException()
			throws SensorReadException {
		new MeasuredConnection(null, null);
	}

	@Test
	public void testOverrideMethodsGetRelationSensorAndGetMeasuredRelationSensorExpectingTheGivenTestSensor()
			throws SensorReadException {
		TransformationSensor testTransformationSensor = new MockTransformationSensor(new TestRuntime());
		RelationSensor testRelationSensor = new RelationSensor(testTransformationSensor, null, null);

		MeasuredConnection testMeasuredConnection = new MeasuredConnection(testRelationSensor, null);

		assertSame(testRelationSensor, testMeasuredConnection.getRelationSensor());

		assertSame(testRelationSensor, testMeasuredConnection.getMeasuredRelationSensor());
	}

	@Test
	public void testOverrideMethodsGetVelocitySensorAndGetMeasuredVelocitySensorExpectingTheGivenTestSensor()
			throws SensorReadException {
		RelationSensor testRelationSensor = new RelationSensor(new MockTransformationSensor(null), null, null);

		VelocitySensor testVelocitySensor = new MockVelocitySensor(null, null, null, null, null);

		MeasuredConnection testMeasuredConnection = new MeasuredConnection(testRelationSensor, testVelocitySensor);

		assertSame(testVelocitySensor, testMeasuredConnection.getVelocitySensor());

		assertSame(testVelocitySensor, testMeasuredConnection.getMeasuredVelocitySensor());
	}
}
