/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorDataAgeSensor;

public class SensorDataAgeSensorTest {
	private SensorDataAgeSensor testSensor = null;
	private DoubleSensor mockDoubleSensor = null;

	@Before
	public void setup() {
		mockDoubleSensor = new DoubleFromJavaSensor(0);
		testSensor = new SensorDataAgeSensor(mockDoubleSensor);
	}

	@After
	public void teardown() {
		testSensor = null;
		mockDoubleSensor = null;
	}

	@Test
	public void testEqualsWithMockDoubleSensor() {
		SensorDataAgeSensor mockSensor = new SensorDataAgeSensor(mockDoubleSensor);
		assertTrue(testSensor.equals(mockSensor));
	}

	@Test
	public void testGetSensorWithMockSensor() {
		assertNotNull(testSensor.getSensor());
	}

	@Test
	public void testHashCodeSimpleTest() {
		assertFalse(Double.isNaN(testSensor.hashCode()));
	}

	@Test
	public void testToStringSimpleTest() {
		assertNotNull(testSensor.toString());
	}

	@Test
	public void testIsAvailableSimpleTest() {
		assertTrue(testSensor.isAvailable());
	}
}
