/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.actuator.OverrideParameter.Scaling;
import org.roboticsapi.core.sensor.DoubleSensor;

public class OverrideParameterTest {
	class MockDeviceParameters implements DeviceParameters {
		@Override
		public boolean respectsBounds(DeviceParameters boundingObject) {
			return false;
		}
	}

	private OverrideParameter testOverrideParameter = null;

	@Before
	public void setup() {
		testOverrideParameter = new OverrideParameter(1, Scaling.ABSOLUTE);
	}

	@After
	public void teardown() {
		testOverrideParameter = null;
	}

	@Test
	public void testRespectBoundsWithInvalidArgumentsExpectingExceptions() {
		try {
			testOverrideParameter.respectsBounds(null);

			fail("The expected IllegalArgumentException didn't occure.");
		} catch (IllegalArgumentException e) {
		}

		try {
			testOverrideParameter.respectsBounds(new MockDeviceParameters());

			fail("The expected IllegalArgumentException didn't occure.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testRespectBoundsWithItself() {
		OverrideParameter testOverrideParameter = new OverrideParameter(1, null);

		assertFalse(testOverrideParameter.respectsBounds(testOverrideParameter));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullDoubleSensorExpectingException() {
		DoubleSensor mockSensor = null;

		new OverrideParameter(mockSensor, null);
	}

	@Test
	public void testRespectBoundsWithTwoDifferentOverrideParameters() {
		OverrideParameter anotherOverrideParameter = new OverrideParameter(1, Scaling.RELATIVE);

		assertTrue(testOverrideParameter.respectsBounds(anotherOverrideParameter));
	}

	@Test
	public void testRespectBoundsWithTwoEqualOverrideParameters() {
		OverrideParameter anotherOverrideParameter = new OverrideParameter(2, Scaling.ABSOLUTE);

		assertTrue(testOverrideParameter.respectsBounds(anotherOverrideParameter));
	}
}
