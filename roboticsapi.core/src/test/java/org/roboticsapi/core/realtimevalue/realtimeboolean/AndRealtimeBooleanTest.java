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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeBoolean;

public class AndRealtimeBooleanTest {

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

	protected void testAndWithTruthTable(RealtimeBoolean trueValue, RealtimeBoolean falseValue)
			throws RealtimeValueReadException {
		RealtimeBoolean and = null;

		and = trueValue.and(trueValue);
		Assert.assertTrue(getValue(and));

		and = trueValue.and(falseValue);
		Assert.assertFalse(getValue(and));

		and = falseValue.and(trueValue);
		Assert.assertFalse(getValue(and));

		and = falseValue.and(falseValue);
		Assert.assertFalse(getValue(and));
	}

	@Test
	public void testCheapValue() {
		RealtimeBoolean bool = new MockRealtimeBoolean();
		AndRealtimeBoolean and = new AndRealtimeBoolean(bool);

		assertNull(and.getCheapValue());
		assertTrue(and.getInnerValues().size() == 1);
		assertTrue(and.getInnerValues().contains(bool));

		bool = new MockRealtimeBoolean(true);
		and = new AndRealtimeBoolean(bool);

		assertNotNull(and.getCheapValue());
		assertTrue(and.getInnerValues().size() == 1);
		assertTrue(and.getInnerValues().contains(bool));

		bool = new MockRealtimeBoolean(false);
		and = new AndRealtimeBoolean(bool);

		assertNotNull(and.getCheapValue());
		assertTrue(and.getInnerValues().size() == 1);
		assertTrue(and.getInnerValues().contains(bool));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNoValues() {
		new AndRealtimeBoolean();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithEmptyCollection() {
		new AndRealtimeBoolean(new ArrayList<>());
	}

	@Test
	public void testIsAvailable() {
		MockRealtimeBoolean bool = new MockRealtimeBoolean();
		AndRealtimeBoolean and = new AndRealtimeBoolean(bool);

		bool.setAvailable(true);
		assertTrue(and.isAvailable());

		bool.setAvailable(false);
		assertFalse(and.isAvailable());
	}

	@Test
	public void testToStringExpectingNotNull() {
		RealtimeBoolean bool = new MockRealtimeBoolean();
		AndRealtimeBoolean and = new AndRealtimeBoolean(bool);

		assertNotNull(and.toString());
	}

	@Test
	public void testAndChainsEvaluateCorrectly() throws RealtimeValueReadException {
		RealtimeBoolean true1 = RealtimeBoolean.TRUE;
		RealtimeBoolean true2 = RealtimeBoolean.TRUE;
		RealtimeBoolean true3 = RealtimeBoolean.TRUE;

		RealtimeBoolean false1 = RealtimeBoolean.FALSE;

		Assert.assertTrue(true1.and(true2).getCurrentValue());
		Assert.assertTrue(true1.and(true2).and(true3).getCurrentValue());
		Assert.assertFalse(true1.and(false1).getCurrentValue());
		Assert.assertFalse(true1.and(true2).and(false1).getCurrentValue());
	}

	@Test
	public void testAndWithTwoValues() throws RealtimeValueReadException {
		testAndWithTruthTable(trueValue, falseValue);
	}

	@Test
	public void testAndWithTwoConstants() throws RealtimeValueReadException {
		testAndWithTruthTable(trueConstant, falseConstant);
	}

	@Test
	public void testAndWithValueAndConstant() throws RealtimeValueReadException {
		testAndWithTruthTable(trueValue, falseConstant);
		testAndWithTruthTable(trueConstant, falseValue);
	}

	@Test
	public void testAndWithAnotherAnd() throws RealtimeValueReadException {
		RealtimeBoolean and1 = trueValue.and(createValue(false));
		RealtimeBoolean and2 = falseValue.and(createValue(true));
		RealtimeBoolean v = createValue(true);

		RealtimeBoolean and = v.and(and1);

		Assert.assertFalse(and.isConstant());
		Assert.assertEquals(and.getClass(), AndRealtimeBoolean.class);

		if (and instanceof AndRealtimeBoolean) {
			AndRealtimeBoolean orBoolean = (AndRealtimeBoolean) and;
			List<RealtimeBoolean> list = orBoolean.getInnerValues();

			Assert.assertEquals(3, list.size());
		}

		and = and1.and(and2);

		Assert.assertFalse(and.isConstant());
		Assert.assertEquals(and.getClass(), AndRealtimeBoolean.class);

		if (and instanceof AndRealtimeBoolean) {
			AndRealtimeBoolean orBoolean = (AndRealtimeBoolean) and;
			List<RealtimeBoolean> list = orBoolean.getInnerValues();

			Assert.assertEquals(4, list.size());
		}
	}

	@Test
	public void testAndCommutative() {
		RealtimeBoolean bool1 = trueValue;
		RealtimeBoolean bool2 = falseValue;

		Assert.assertNotEquals(bool1, bool2);

		RealtimeBoolean and1 = bool1.and(bool2);
		RealtimeBoolean and2 = bool2.and(bool1);

		assertEquals(and1, and2);
	}

}
