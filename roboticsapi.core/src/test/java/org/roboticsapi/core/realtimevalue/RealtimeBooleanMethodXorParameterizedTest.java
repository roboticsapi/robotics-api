/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.WritableRealtimeBoolean;

@RunWith(Parameterized.class)
public class RealtimeBooleanMethodXorParameterizedTest {
	private final boolean testLeftValue;
	private final boolean testRightValue;
	private final boolean expectedResult;

	private WritableRealtimeBoolean testLeftSensor = null;

	public RealtimeBooleanMethodXorParameterizedTest(boolean left, boolean right, boolean expected) {
		testLeftValue = left;
		testRightValue = right;
		expectedResult = expected;
	}

	@Parameterized.Parameters(name = "[{0}; {1}]; {2}")
	public static Collection<Object[]> testValues() {
		Object[][] testValues = new Object[4][3];
		testValues[0] = new Object[] { true, true, false };
		testValues[1] = new Object[] { true, false, true };
		testValues[2] = new Object[] { false, true, true };
		testValues[3] = new Object[] { false, false, false };

		return Arrays.asList(testValues);
	}

	@Before
	public void setup() {
		testLeftSensor = RealtimeBoolean.createWritable(testLeftValue);
	}

	@After
	public void teardown() {
		testLeftSensor = null;
	}

	@Test
	public void testMethodNamedXorWithBooleanParameterWithParameterizedValuesExpectingCorrectXorResult() {
		assertEquals(expectedResult,
				testLeftSensor.xor(RealtimeBoolean.createFromConstant(testRightValue)).getCheapValue());
	}
}
