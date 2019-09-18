/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimeinteger.AbsRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

public class AbsRealtimeIntegerTest {
	private AbsRealtimeInteger mockSensor = null;
	private RealtimeInteger mockInnerSensor = null;

	@Before
	public void setup() {
		mockInnerSensor = RealtimeInteger.createWritable(1);
		mockSensor = mockInnerSensor.abs();
	}

	@After
	public void teardown() {
		mockSensor = null;
		mockInnerSensor = null;
	}

	@Test
	public void testGetInnerSensorSimpleTest() {
		assertSame(mockInnerSensor, mockSensor.getInnerValue());
	}

	@Test
	public void testToStringSimpleTest() {
		assertNotNull(mockSensor.toString());
	}

	@Test
	public void testComputeCheapValueByUsingMethodGetCheapValueSimpleTests() {
		assertSame(1, mockSensor.getCheapValue());

		mockSensor = RealtimeInteger.createWritable(-2).abs();

		assertSame(2, mockSensor.getCheapValue());
	}
}
