/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;

public class ConstantRealtimeBooleanTest {

	@Test
	public void testCheapValue() {
		ConstantRealtimeBoolean constant = new ConstantRealtimeBoolean(true);
		assertNotNull(constant.getCheapValue());

		constant = new ConstantRealtimeBoolean(false);
		assertNotNull(constant.getCheapValue());
	}

	@Test
	public void testCurrentValue() throws RealtimeValueReadException {
		ConstantRealtimeBoolean constant = new ConstantRealtimeBoolean(true);
		assertNotNull(constant.getCurrentValue());

		constant = new ConstantRealtimeBoolean(false);
		assertNotNull(constant.getCurrentValue());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorWithNull() throws RealtimeValueReadException {
		new ConstantRealtimeBoolean(null);
	}

	@Test
	public void testEquals() {
		ConstantRealtimeBoolean constant1 = new ConstantRealtimeBoolean(true);
		ConstantRealtimeBoolean constant2 = new ConstantRealtimeBoolean(true);
		assertEquals(constant1, constant2);

		constant1 = new ConstantRealtimeBoolean(false);
		constant2 = new ConstantRealtimeBoolean(false);
		assertEquals(constant1, constant2);

		constant1 = new ConstantRealtimeBoolean(true);
		constant2 = new ConstantRealtimeBoolean(false);
		assertNotEquals(constant1, constant2);

		constant1 = new ConstantRealtimeBoolean(false);
		constant2 = new ConstantRealtimeBoolean(true);
		assertNotEquals(constant1, constant2);
	}

	@Test
	public void testIsAvailable() {
		ConstantRealtimeBoolean constant = new ConstantRealtimeBoolean(true);
		assertTrue(constant.isAvailable());

		constant = new ConstantRealtimeBoolean(false);
		assertTrue(constant.isAvailable());
	}

	@Test
	public void testToStringExpectingNotNull() {
		ConstantRealtimeBoolean constant = new ConstantRealtimeBoolean(true);
		assertNotNull(constant.toString());

		constant = new ConstantRealtimeBoolean(false);
		assertNotNull(constant.toString());
	}

	@Test
	public void testToStringExpectingDifferentMessages() {
		ConstantRealtimeBoolean constant1 = new ConstantRealtimeBoolean(true);
		ConstantRealtimeBoolean constant2 = new ConstantRealtimeBoolean(false);

		assertNotEquals(constant1.toString(), constant2.toString());
	}

	@Test
	public void testIsConstantEvaluatesCorrectly() {
		assertTrue(new ConstantRealtimeBoolean(true).isConstant());
	}

}
