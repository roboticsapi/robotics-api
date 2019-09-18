/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AddedRealtimeIntegerTest {
	// Tests for subclass AddedRealtimeInteger:

	@Test
	public void testMethodGetAddend1OfSubclassAddedRealtimeIntegerWithTestSensorExpectingTheTestSensorSimpleTest() {
		RealtimeInteger testRealtimeInteger1 = RealtimeInteger.createWritable(1);

		RealtimeInteger testAddedRealtimeInteger = testRealtimeInteger1.add(RealtimeInteger.createWritable(2));

		assertTrue(testAddedRealtimeInteger instanceof AddedRealtimeInteger);

		AddedRealtimeInteger testAddedRealtimeInteger2 = (AddedRealtimeInteger) testAddedRealtimeInteger;

		assertNotNull(testAddedRealtimeInteger2.getAddend1());

		assertEquals(testRealtimeInteger1, testAddedRealtimeInteger2.getAddend1());
	}

	@Test
	public void testMethodGetAddend2OfSubclassAddedRealtimeIntegerWithTestSensorExpectingTheTestSensorSimpleTest() {
		RealtimeInteger testRealtimeInteger2 = RealtimeInteger.createWritable(2);

		AddedRealtimeInteger testAddedRealtimeInteger = new AddedRealtimeInteger(new WritableRealtimeInteger(1),
				testRealtimeInteger2);

		assertNotNull(testAddedRealtimeInteger.getAddend2());

		assertEquals(testRealtimeInteger2, testAddedRealtimeInteger.getAddend2());
	}

	@Test
	public void testOverrideMethodToStringOfSubclassAddedRealtimeIntegerExpectingNeitherNullNorEmptySimpleTest() {
		AddedRealtimeInteger testAddedRealtimeInteger = new AddedRealtimeInteger(new WritableRealtimeInteger(0),
				new WritableRealtimeInteger(1));

		assertNotNull(testAddedRealtimeInteger.toString());

		assertFalse(testAddedRealtimeInteger.toString().isEmpty());
	}
}
