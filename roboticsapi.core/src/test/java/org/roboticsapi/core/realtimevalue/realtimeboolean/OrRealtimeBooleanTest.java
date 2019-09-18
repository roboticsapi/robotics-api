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

public class OrRealtimeBooleanTest {

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
		OrRealtimeBoolean or = new OrRealtimeBoolean(bool);

		assertNull(or.getCheapValue());
		assertTrue(or.getInnerValues().size() == 1);
		assertTrue(or.getInnerValues().contains(bool));

		bool = new MockRealtimeBoolean(true);
		or = new OrRealtimeBoolean(bool);

		assertNotNull(or.getCheapValue());
		assertTrue(or.getInnerValues().size() == 1);
		assertTrue(or.getInnerValues().contains(bool));

		bool = new MockRealtimeBoolean(false);
		or = new OrRealtimeBoolean(bool);

		assertNotNull(or.getCheapValue());
		assertTrue(or.getInnerValues().size() == 1);
		assertTrue(or.getInnerValues().contains(bool));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNoValues() {
		new OrRealtimeBoolean();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithEmptyCollection() {
		new OrRealtimeBoolean(new ArrayList<>());
	}

	@Test
	public void testIsAvailable() {
		MockRealtimeBoolean bool = new MockRealtimeBoolean();
		OrRealtimeBoolean or = new OrRealtimeBoolean(bool);

		bool.setAvailable(true);
		assertTrue(or.isAvailable());

		bool.setAvailable(false);
		assertFalse(or.isAvailable());
	}

	@Test
	public void testToStringExpectingNotNull() {
		RealtimeBoolean bool = new MockRealtimeBoolean();
		OrRealtimeBoolean or = new OrRealtimeBoolean(bool);

		assertNotNull(or.toString());
	}

	@Test
	public void testOrWithTwoValues() throws RealtimeValueReadException {
		testOrWithTruthTable(trueValue, falseValue);
	}

	@Test
	public void testOrWithTwoConstants() throws RealtimeValueReadException {
		testOrWithTruthTable(trueConstant, falseConstant);
	}

	@Test
	public void testOrWithValueAndConstant() throws RealtimeValueReadException {
		testOrWithTruthTable(trueValue, falseConstant);
		testOrWithTruthTable(trueConstant, falseValue);
	}

	protected void testOrWithTruthTable(RealtimeBoolean trueValue, RealtimeBoolean falseValue)
			throws RealtimeValueReadException {
		RealtimeBoolean or = null;

		or = trueValue.or(trueValue);
		Assert.assertTrue(getValue(or));

		or = trueValue.or(falseValue);
		Assert.assertTrue(getValue(or));

		or = falseValue.or(trueValue);
		Assert.assertTrue(getValue(or));

		or = falseValue.or(falseValue);
		Assert.assertFalse(getValue(or));
	}

	@Test
	public void testOrWithNonEqualValues() throws RealtimeValueReadException {
		RealtimeBoolean or = trueValue.or(falseValue, createValue(true), createValue(false));

		Assert.assertFalse(or.isConstant());
		Assert.assertEquals(or.getClass(), OrRealtimeBoolean.class);

		if (or instanceof OrRealtimeBoolean) {
			OrRealtimeBoolean orBoolean = (OrRealtimeBoolean) or;
			List<RealtimeBoolean> list = orBoolean.getInnerValues();

			Assert.assertEquals(4, list.size());
		}
	}

	@Test
	public void testOrWithSomeEqualValues() throws RealtimeValueReadException {
		RealtimeBoolean or = trueValue.or(falseValue, trueValue, falseValue);

		Assert.assertFalse(or.isConstant());
		Assert.assertEquals(or.getClass(), OrRealtimeBoolean.class);

		if (or instanceof OrRealtimeBoolean) {
			OrRealtimeBoolean orBoolean = (OrRealtimeBoolean) or;
			List<RealtimeBoolean> list = orBoolean.getInnerValues();

			Assert.assertEquals(2, list.size());
		}
	}

	@Test
	public void testOrWithOnlyEqualValues() throws RealtimeValueReadException {
		RealtimeBoolean or = trueValue.or(trueValue, trueValue, trueValue);

		Assert.assertFalse(or.isConstant());
		Assert.assertEquals(or, trueValue);
	}

	@Test
	public void testOrWithDifferentValuesAndOneConstantTrue() throws RealtimeValueReadException {
		RealtimeBoolean or = trueValue.or(falseValue, createConstant(true), createValue(false));

		Assert.assertTrue(getValue(or));
	}

	@Test
	public void testOrWithAnotherOr() throws RealtimeValueReadException {
		RealtimeBoolean or1 = trueValue.or(createValue(false));
		RealtimeBoolean or2 = falseValue.or(createValue(true));
		RealtimeBoolean v = createValue(true);

		RealtimeBoolean or = v.or(or1);

		Assert.assertFalse(or.isConstant());
		Assert.assertEquals(or.getClass(), OrRealtimeBoolean.class);

		if (or instanceof OrRealtimeBoolean) {
			OrRealtimeBoolean orBoolean = (OrRealtimeBoolean) or;
			List<RealtimeBoolean> list = orBoolean.getInnerValues();

			Assert.assertEquals(3, list.size());
		}

		or = or1.or(or2);

		Assert.assertFalse(or.isConstant());
		Assert.assertEquals(or.getClass(), OrRealtimeBoolean.class);

		if (or instanceof OrRealtimeBoolean) {
			OrRealtimeBoolean orBoolean = (OrRealtimeBoolean) or;
			List<RealtimeBoolean> list = orBoolean.getInnerValues();

			Assert.assertEquals(4, list.size());
		}
	}

	@Test
	public void testOrCommutative() {
		RealtimeBoolean bool1 = trueValue;
		RealtimeBoolean bool2 = falseValue;

		Assert.assertNotEquals(bool1, bool2);

		RealtimeBoolean or1 = bool1.or(bool2);
		RealtimeBoolean or2 = bool2.or(bool1);

		assertEquals(or1, or2);
	}

}
