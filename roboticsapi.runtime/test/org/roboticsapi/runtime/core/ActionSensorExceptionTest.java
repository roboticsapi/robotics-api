/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.runtime.ActionSensorException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.SensorRealtimeException;
import org.roboticsapi.runtime.mockclass.MockAction;

public class ActionSensorExceptionTest {
	private ActionSensorException mockExc;

	@Before
	public void setup() {
		mockExc = new ActionSensorException(null, new SensorRealtimeException(new DoubleFromJavaSensor(0)));
	}

	@After
	public void teardown() {
		mockExc = null;
	}

	@Test
	public void testConstructorSimpleTest() {
		assertNotNull(mockExc);
		assertNotNull(mockExc.getExceptionState());
	}

	@Test
	public void testOverrideEqualsSimpleTests() {
		String s = "test";
		assertFalse(mockExc.equals(s));

		ActionSensorException excFalse = new ActionSensorException(null,
				new SensorRealtimeException(new DoubleFromJavaSensor(1)));
		assertFalse(mockExc.equals(excFalse));

		ActionSensorException excTrue = new ActionSensorException(null, new SensorRealtimeException(null));

		assertTrue(mockExc.equals(excTrue));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullSensorRealtimeExceptionExpectingIllegalArgumentException() {
		new ActionSensorException(new MockAction(1), null);
	}
}
