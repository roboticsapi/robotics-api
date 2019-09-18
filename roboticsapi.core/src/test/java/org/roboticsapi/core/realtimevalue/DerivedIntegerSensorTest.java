/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.mock.MockDerivedRealtimeInteger;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.DerivedRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

public class DerivedIntegerSensorTest {
	private DerivedRealtimeInteger mockSensor = null;

	@Before
	public void setup() {
		mockSensor = new MockDerivedRealtimeInteger(RealtimeInteger.createWritable(0));
	}

	@After
	public void teardown() {
		mockSensor = null;
	}

	@Test
	public void testIsAvailableSimpleTest() {
		assertTrue(mockSensor.isAvailable());
	}

	@Test
	public void testCalculateCheapValueByUsingMethodGetCheapValueSimpleTest() {
		assertSame(0, mockSensor.getCheapValue());
	}

	@Test
	public void testCalculateCheapValueByUsingMethodGetCheapValueWithTestSensorWithNullCheapValue() {
		mockSensor = new MockDerivedRealtimeInteger(new MockRealtimeInteger(null));

		assertNull(mockSensor.getCheapValue());
	}

	@Test
	public void testEqualsWithInvalidCompareObjectExpectingFalse() {
		assertFalse(mockSensor.equals(new Object()));
	}

	@Test
	public void testEqualsWithAnotherDerivedIntegerSensorButInvalidInnerSensorExpectingFalse() {
		DerivedRealtimeInteger otherSensor = new MockDerivedRealtimeInteger(new MockRealtimeInteger(null));

		assertFalse(mockSensor.equals(otherSensor));
	}

	@Test
	public void testEqualsWithAnotherDerivedIntegerSensorWithSameInnerSensorExpectingTrue() {
		RealtimeInteger testInnerSensor = RealtimeInteger.createWritable(0);
		mockSensor = new MockDerivedRealtimeInteger(testInnerSensor);
		DerivedRealtimeInteger otherSensor = new MockDerivedRealtimeInteger(testInnerSensor);

		assertTrue(mockSensor.equals(otherSensor));
	}

	@Test
	public void testHashCodeSimplyByCalling() {
		mockSensor.hashCode();
	}
}
