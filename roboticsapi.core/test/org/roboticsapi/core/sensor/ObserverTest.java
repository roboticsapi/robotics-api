/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.Observer;
import org.roboticsapi.mockclass.MockSensorListenerImpl;

public class ObserverTest {
	private Observer<Boolean> testBoolObserver = null;

	@After
	public void teardown() {
		testBoolObserver = null;
	}

	@Test
	public void testConstructorWithGenericBooleanWithSensorAndSensorListenerArgumentExpectingGetCommandReturnsNullAndGetListenerEqualsTestListener() {
		Sensor<Boolean> testSensor = new BooleanFromJavaSensor(true);
		SensorListener<Boolean> testListener = new MockSensorListenerImpl<Boolean>();

		testBoolObserver = new Observer<Boolean>(testSensor, testListener);

		assertFalse(testBoolObserver.isAsync());

		assertNotNull(testBoolObserver);

		assertNull(testBoolObserver.getCommand());

		assertEquals(testListener, testBoolObserver.getListener());
	}

}
