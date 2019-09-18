/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeBoolean;

public class NegatedRealtimeBooleanTest {

	private RealtimeBoolean trueValue, falseValue;
	private RealtimeBoolean trueConstant, falseConstant;

	@Before
	public void setup() {
		trueValue = createValue(true);
		falseValue = createValue(false);

		trueConstant = createConstant(true);
		falseConstant = createConstant(false);
	}

	private static WritableRealtimeBoolean createValue(boolean value) {
		return new WritableRealtimeBoolean(value);
	}

	private static ConstantRealtimeBoolean createConstant(boolean constant) {
		return new ConstantRealtimeBoolean(constant);
	}

	protected boolean getValue(RealtimeBoolean value) throws RealtimeValueReadException {
		return value.getCurrentValue();
	}

	@Test
	public void testCheapValue() {
		RealtimeBoolean bool = new MockRealtimeBoolean();
		NegatedRealtimeBoolean not = new NegatedRealtimeBoolean(bool);

		assertNull(not.getCheapValue());
		assertEquals(not.getOther(), bool);

		bool = new MockRealtimeBoolean(true);
		not = new NegatedRealtimeBoolean(bool);

		assertNotNull(not.getCheapValue());
		assertEquals(not.getOther(), bool);

		bool = new MockRealtimeBoolean(false);
		not = new NegatedRealtimeBoolean(bool);

		assertNotNull(not.getCheapValue());
		assertEquals(not.getOther(), bool);
	}

	@Test
	public void testEquals() {
		RealtimeBoolean bool = new MockRealtimeBoolean();
		NegatedRealtimeBoolean not1 = new NegatedRealtimeBoolean(bool);
		NegatedRealtimeBoolean not2 = new NegatedRealtimeBoolean(bool);

		assertEquals(not1, not2);
	}

	@Test
	public void testIsAvailable() {
		MockRealtimeBoolean bool = new MockRealtimeBoolean();
		NegatedRealtimeBoolean not = new NegatedRealtimeBoolean(bool);

		bool.setAvailable(true);
		assertTrue(not.isAvailable());

		bool.setAvailable(false);
		assertFalse(not.isAvailable());
	}

	@Test
	public void testToStringExpectingNotNull() {
		RealtimeBoolean bool = new MockRealtimeBoolean();
		NegatedRealtimeBoolean not = new NegatedRealtimeBoolean(bool);

		assertNotNull(not.toString());
	}

	@Test
	public void testNotWithValues() throws RealtimeValueReadException {
		RealtimeBoolean not = trueValue.not();
		Assert.assertFalse(getValue(not));
		Assert.assertFalse(not.isConstant());

		not = not.not();
		Assert.assertEquals(not, trueValue);

		not = falseValue.not();
		Assert.assertTrue(getValue(not));
		Assert.assertFalse(not.isConstant());

		not = not.not();
		Assert.assertEquals(not, falseValue);
	}

	@Test
	public void testNotWithConstants() throws RealtimeValueReadException {
		RealtimeBoolean not = trueConstant.not();
		Assert.assertFalse(getValue(not));
		Assert.assertTrue(not.isConstant());

		not = not.not();
		Assert.assertEquals(not, trueConstant);

		not = falseConstant.not();
		Assert.assertTrue(getValue(not));
		Assert.assertTrue(not.isConstant());

		not = not.not();
		Assert.assertEquals(not, falseConstant);
	}

}
