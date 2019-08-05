/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.mockclass.MockTransformationSensor;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class DynamicConnectionTest {
	private class MockDynamicConnection extends DynamicConnection {
		private RelationSensor relationSensor = null;

		public void setRelationSensor(RelationSensor rs) {
			relationSensor = rs;
		}

		@Override
		public RelationSensor getRelationSensor() {
			return relationSensor;
		}

		@Override
		public RelationSensor getMeasuredRelationSensor() {
			return getRelationSensor();
		}

		@Override
		public VelocitySensor getVelocitySensor() {
			return null;
		}

		@Override
		public VelocitySensor getMeasuredVelocitySensor() {
			return getVelocitySensor();
		}
	}

	private MockDynamicConnection testDynamicConnection = null;

	@Before
	public void setup() {
		testDynamicConnection = new MockDynamicConnection();
	}

	@After
	public void teardown() {
		testDynamicConnection = null;
	}

	private void setRelationSensorOfTestDynamicConnection(TransformationSensor testTransformationSensor) {
		Frame fromFrame = new Frame();
		Frame toFrame = new Frame();

		RelationSensor testRelationSensor = new RelationSensor(testTransformationSensor, fromFrame, toFrame);

		testDynamicConnection.setRelationSensor(testRelationSensor);
	}

	@Test
	public void testGetTransformationWithValidRelationSensorExpectingNotNull() throws TransformationException {
		setRelationSensorOfTestDynamicConnection(new MockTransformationSensor(null));

		assertNotNull(testDynamicConnection.getTransformation());

		assertNotNull(testDynamicConnection.getTransformation().getRotation());

		assertNotNull(testDynamicConnection.getTransformation().getTranslation());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetTransformationWithoutLinkingARelationSensorExpectingUnsupportedOperationException()
			throws TransformationException {
		testDynamicConnection.getTransformation();
	}

	private class SpecialMockTransformationSensor extends MockTransformationSensor {
		public SpecialMockTransformationSensor(RoboticsRuntime runtime) {
			super(runtime);
		}

		@Override
		protected Transformation calculateCheapValue() {
			return null;
		}
	}

	@Test(expected = TransformationException.class)
	public void testGetTransformationExpectingTransformationException() throws TransformationException {
		setRelationSensorOfTestDynamicConnection(new SpecialMockTransformationSensor(null));

		testDynamicConnection.getTransformation();
	}

	@Test
	public void testGetMeasuredTransformationWithValidRelationSensorExpectingNotNull() throws TransformationException {
		setRelationSensorOfTestDynamicConnection(new MockTransformationSensor(null));

		assertNotNull(testDynamicConnection.getMeasuredTransformation());

		assertNotNull(testDynamicConnection.getMeasuredTransformation().getRotation());

		assertNotNull(testDynamicConnection.getMeasuredTransformation().getTranslation());
	}

	@Test(expected = TransformationException.class)
	public void testGetMeasuredTransformationExpectingTransformationException() throws TransformationException {
		setRelationSensorOfTestDynamicConnection(new SpecialMockTransformationSensor(null));

		testDynamicConnection.getMeasuredTransformation();
	}
}
