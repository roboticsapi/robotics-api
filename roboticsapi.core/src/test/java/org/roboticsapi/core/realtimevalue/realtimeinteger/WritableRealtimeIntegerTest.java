/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WritableRealtimeIntegerTest {

	private WritableRealtimeInteger mockSensor = null;

	@Before
	public void setup() {
		mockSensor = RealtimeInteger.createWritable(0);
	}

	@After
	public void teardown() {
		mockSensor = null;
	}

	@Test
	public void testIsAvailableOfSubclassRealtimeIntegerFromJavaSimpleTest() {
		assertTrue(mockSensor.isAvailable());
	}

	@Test
	public void testToStringOfSubclassRealtimeIntegerFromJavaSimpleTest() {
		assertNotNull(mockSensor.toString());
	}
}
