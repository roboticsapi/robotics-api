/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mockclass.MockRoboticsRuntime;
import org.roboticsapi.runtime.mockclass.MockSensorListenerImpl;
import org.roboticsapi.runtime.mockclass.MockTransformationSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;

public abstract class AbstractRelationSensorTest extends AbstractRuntimeTest {
	@Test(timeout = 3000)
	public void testSensorListenersCachedForStaticConnection() throws MappingException, InitializationException {
		StaticConnection c = new StaticConnection();
		Frame from = new Frame();
		c.setFrom(from);
		Frame to = new Frame();
		c.setTo(to);

		RoboticsContext context = new RoboticsContextImpl("test");

		context.initialize(from);
		context.initialize(to);
		context.initialize(c);

		NetFragment fragment = new NetFragment("testSensorListenersCachedForStaticConnection");
		SensorMappingContext smc = new SensorMappingContext();
		SoftRobotRuntime runtime = (SoftRobotRuntime) getRuntime();
		RelationSensor relationSensor1 = c.getRelationSensor();
		runtime.getMapperRegistry().mapSensor(getRuntime(), relationSensor1, fragment, smc);
		RelationSensor relationSensor2 = c.getRelationSensor();
		runtime.getMapperRegistry().mapSensor(getRuntime(), relationSensor2, fragment, smc);
		SensorMapperResult<?> sensorResult1 = smc.getSensorResult(relationSensor1);
		SensorMapperResult<?> sensorResult2 = smc.getSensorResult(relationSensor2);

		Assert.assertEquals(sensorResult1, sensorResult2);

		context.destroy();
	}

	@Test(timeout = 3000)
	public void testOverrideMethodsAddListenerAndRemoveListenerWithMockRuntimeSimpleTest() throws RoboticsException {
		RelationSensor testSensor = new RelationSensor(new MockTransformationSensor(new MockRoboticsRuntime()), null,
				null);

		SensorListener<Transformation> testListener = new MockSensorListenerImpl<Transformation>();

		testSensor.addListener(testListener);

		testSensor.removeListener(testListener);
	}

	@Test(timeout = 3000)
	public void testReinterpretExpectingReinterpretedTransformationSensorIsSameAsOriginalTransformationSensor() {
		RelationSensor testSensor = new RelationSensor(new MockTransformationSensor(getRuntime()), null, null);

		RelationSensor reinterpretedSensor = testSensor.reinterpret(new Frame(), new Frame());

		assertSame(testSensor.getTransformationSensor(), reinterpretedSensor.getTransformationSensor());
	}

	@Test(timeout = 3000)
	public void testGetXExpectingEqualValueAsTransformationSensorGetXSimpleTest() {
		TransformationSensor testTrafoSensor = new MockTransformationSensor(getRuntime());

		RelationSensor testSensor = new RelationSensor(testTrafoSensor, null, null);

		assertEquals(testTrafoSensor.getX(), testSensor.getX());
	}

	@Test(timeout = 3000)
	public void testGetYExpectingEqualValueAsTransformationSensorGetYSimpleTest() {
		TransformationSensor testTrafoSensor = new MockTransformationSensor(getRuntime());

		RelationSensor testSensor = new RelationSensor(testTrafoSensor, null, null);

		assertEquals(testTrafoSensor.getY(), testSensor.getY());
	}

	@Test(timeout = 3000)
	public void testGetZExpectingEqualValueAsTransformationSensorGetZSimpleTest() {
		TransformationSensor testTrafoSensor = new MockTransformationSensor(getRuntime());

		RelationSensor testSensor = new RelationSensor(testTrafoSensor, null, null);

		assertEquals(testTrafoSensor.getZ(), testSensor.getZ());
	}

	@Test(timeout = 3000)
	public void testGetAExpectingEqualValueAsTransformationSensorGetASimpleTest() {
		TransformationSensor testTrafoSensor = new MockTransformationSensor(getRuntime());

		RelationSensor testSensor = new RelationSensor(testTrafoSensor, null, null);

		assertEquals(testTrafoSensor.getA(), testSensor.getA());
	}

	@Test(timeout = 3000)
	public void testGetBExpectingEqualValueAsTransformationSensorGetBSimpleTest() {
		TransformationSensor testTrafoSensor = new MockTransformationSensor(getRuntime());

		RelationSensor testSensor = new RelationSensor(testTrafoSensor, null, null);

		assertEquals(testTrafoSensor.getB(), testSensor.getB());
	}

	@Test(timeout = 3000)
	public void testGetCExpectingEqualValueAsTransformationSensorGetCSimpleTest() {
		TransformationSensor testTrafoSensor = new MockTransformationSensor(getRuntime());

		RelationSensor testSensor = new RelationSensor(testTrafoSensor, null, null);

		assertEquals(testTrafoSensor.getC(), testSensor.getC());
	}
}
