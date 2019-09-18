/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimeinteger.DividedRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.WritableRealtimeInteger;

public class DividedRealtimeIntegerTest {
	private RealtimeInteger mockSensor = null;
	private WritableRealtimeInteger mockDividend = null;
	private WritableRealtimeInteger mockDivisor = null;

	@Before
	public void setup() {
		mockDividend = RealtimeInteger.createWritable(2);
		mockDivisor = RealtimeInteger.createWritable(1);
		mockSensor = mockDividend.divide(mockDivisor);
	}

	@After
	public void teardown() {
		mockSensor = null;
		mockDividend = null;
		mockDivisor = null;
	}

	@Test
	public void testDivideHasExpectedStructure() {
		assertTrue(mockSensor instanceof DividedRealtimeInteger);

		DividedRealtimeInteger mockSensor2 = (DividedRealtimeInteger) mockSensor;

		assertSame(mockDividend, mockSensor2.getDividend());
		assertSame(mockDivisor, mockSensor2.getDivisor());
	}

	@Test
	public void testToStringSimpleTest() {
		assertNotNull(mockSensor.toString());
	}

	@Test
	public void testComputeCheapValueByUsingGetCurrentValueWithValidValues() throws RealtimeValueReadException {
		assertSame(2, mockSensor.getCurrentValue());

		mockDividend.setValue(3);
		mockDivisor.setValue(2);

		assertSame(1, mockSensor.getCurrentValue());

		mockDividend.setValue(-3);

		assertSame(-1, mockSensor.getCurrentValue());
	}

	@Test(expected = ArithmeticException.class)
	public void testComputeCheapValueByUsingGetCurrentValueWithZeroAsInvalidDivisorExpectingException()
			throws RealtimeValueReadException {
		mockDivisor.setValue(0);
		mockSensor.getCurrentValue();
	}
}
