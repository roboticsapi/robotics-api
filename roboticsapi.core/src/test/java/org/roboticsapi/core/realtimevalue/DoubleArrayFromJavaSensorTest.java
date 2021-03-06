/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.WritableRealtimeDoubleArray;

public class DoubleArrayFromJavaSensorTest {
	private WritableRealtimeDoubleArray mockSensor = null;

	@Before
	public void setup() {
		mockSensor = RealtimeDouble.createWritableArray(new Double[] { 0d, 1d, 2d, 3d });
	}

	@After
	public void teardown() {
		mockSensor = null;
	}

	@Test
	public void testSetValueWithDifferentTestValuesOnlyCalling() {
		mockSensor.setValue(new Double[] { 4d, 5d, 6d, 7d });
	}

	@Test
	public void testIsAvailableSimpleTest() {
		assertTrue(mockSensor.isAvailable());
	}

	@Test
	public void testToStringSimpleTest() {
		assertNotNull(mockSensor.toString());
	}
}
