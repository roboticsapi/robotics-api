/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class RealtimeBooleanTest {

	class SpecialMockBooleanSensor extends MockRealtimeBoolean {
		public SpecialMockBooleanSensor(RoboticsRuntime runtime) {
			super(runtime);
		}
	}

	private RealtimeBoolean trueValue, falseValue;
	private RealtimeBoolean trueConstant, falseConstant;

	private static WritableRealtimeBoolean createValue(boolean value) {
		return new WritableRealtimeBoolean(value);
	}

	private static ConstantRealtimeBoolean createConstant(boolean constant) {
		return new ConstantRealtimeBoolean(constant);
	}

	protected boolean getValue(RealtimeBoolean value) throws RealtimeValueReadException {
		return value.getCurrentValue();
	}

	@Before
	public void setup() {
		trueValue = createValue(true);
		falseValue = createValue(false);

		trueConstant = createConstant(true);
		falseConstant = createConstant(false);
	}

	@After
	public void teardown() {
		trueValue = null;
		falseValue = null;

		trueConstant = null;
		falseConstant = null;
	}

	@Test
	public void testAndHasExpectedStructure() {
		RealtimeBoolean and = trueValue.and(falseValue);

		Assert.assertTrue(and instanceof AndRealtimeBoolean);

		Assert.assertTrue(((AndRealtimeBoolean) and).getInnerValues().contains(trueValue));
		Assert.assertTrue(((AndRealtimeBoolean) and).getInnerValues().contains(falseValue));

	}

	@Test
	public void testAndEvaluatesCorrecly() throws RealtimeValueReadException {
		Assert.assertFalse(falseConstant.and(falseConstant).getCurrentValue());
		Assert.assertFalse(trueConstant.and(falseConstant).getCurrentValue());
		Assert.assertFalse(falseConstant.and(trueConstant).getCurrentValue());
		Assert.assertTrue(trueConstant.and(trueConstant).getCurrentValue());

		Assert.assertTrue(trueConstant.and(trueConstant).and(trueConstant).getCurrentValue());
		Assert.assertFalse(trueConstant.and(trueConstant).and(falseConstant).getCurrentValue());
	}

	@Test
	public void testOrHasExpectedStructure() throws InterruptedException {
		RealtimeBoolean or = trueValue.or(falseValue);

		Assert.assertTrue(or instanceof OrRealtimeBoolean);

		Assert.assertTrue(((OrRealtimeBoolean) or).getInnerValues().contains(trueValue));
		Assert.assertTrue(((OrRealtimeBoolean) or).getInnerValues().contains(falseValue));
	}

	@Test
	public void testOrEvaluatesCorrecly() throws RealtimeValueReadException {
		Assert.assertFalse(falseConstant.or(falseConstant).getCurrentValue());
		Assert.assertTrue(trueConstant.or(falseConstant).getCurrentValue());
		Assert.assertTrue(falseConstant.or(trueConstant).getCurrentValue());
		Assert.assertTrue(trueConstant.or(trueConstant).getCurrentValue());

		Assert.assertTrue(trueConstant.or(falseConstant).or(falseConstant).getCurrentValue());
		Assert.assertFalse(falseConstant.or(falseConstant).or(falseConstant).getCurrentValue());
	}

	@Test
	public void testXorWithTwoValues() throws RealtimeValueReadException {
		testXorWithTruthTable(trueValue, falseValue);
	}

	@Test
	public void testXorWithTwoConstants() throws RealtimeValueReadException {
		testXorWithTruthTable(trueConstant, falseConstant);
	}

	@Test
	public void testXorWithValueAndConstant() throws RealtimeValueReadException {
		testXorWithTruthTable(trueValue, falseConstant);
		testXorWithTruthTable(trueConstant, falseValue);
	}

	protected void testXorWithTruthTable(RealtimeBoolean trueValue, RealtimeBoolean falseValue)
			throws RealtimeValueReadException {
		RealtimeBoolean xor = null;

		xor = trueValue.xor(trueValue);
		Assert.assertFalse(getValue(xor));

		xor = trueValue.xor(falseValue);
		Assert.assertTrue(getValue(xor));

		xor = falseValue.xor(trueValue);
		Assert.assertTrue(getValue(xor));

		xor = falseValue.xor(falseValue);
		Assert.assertFalse(getValue(xor));
	}

	@Test
	public void testNotEvaluatesCorrectly() throws RealtimeValueReadException {
		Assert.assertTrue(falseConstant.not().getCurrentValue());
		Assert.assertFalse(trueConstant.not().getCurrentValue());

	}

	@Test
	public void testIsNullEvaluatesCorrectly() throws RealtimeValueReadException {
		Assert.assertFalse(falseConstant.isNull().getCurrentValue());
		Assert.assertFalse(trueConstant.isNull().getCurrentValue());
	}

	@Test
	public void testfromHistoryWithConstantHasExpectedStructure() {
		RealtimeBoolean fromHistory = falseConstant.fromHistory(1);

		Assert.assertTrue(fromHistory instanceof RealtimeBooleanAtTime);
	}

	@Test
	public void testfromHistoryWithRealtimeValueHasExpectedStructure() {
		RealtimeDouble fromValue = RealtimeDouble.createFromConstant(1);

		RealtimeBoolean fromHistory = falseConstant.fromHistory(fromValue, 2);

		Assert.assertTrue(fromHistory instanceof RealtimeBooleanAtTime);

		Assert.assertEquals(fromValue, ((RealtimeBooleanAtTime) fromHistory).getAge());

		Assert.assertEquals(2, ((RealtimeBooleanAtTime) fromHistory).getMaxAge(), 0.00001);
	}

	@Test
	public void testCreateAndWithNonEqualValues() throws RealtimeValueReadException {
		RealtimeBoolean and = RealtimeBoolean.createAnd(falseValue, trueValue, createValue(true), createValue(false));

		Assert.assertFalse(and.isConstant());
		Assert.assertEquals(and.getClass(), AndRealtimeBoolean.class);

		if (and instanceof AndRealtimeBoolean) {
			AndRealtimeBoolean andBoolean = (AndRealtimeBoolean) and;
			List<RealtimeBoolean> list = andBoolean.getInnerValues();

			Assert.assertEquals(4, list.size());
		}
	}

	@Test
	public void testCreateAndWithSomeEqualValues() throws RealtimeValueReadException {
		RealtimeBoolean and = RealtimeBoolean.createAnd(falseValue, trueValue, falseValue, trueValue);

		Assert.assertFalse(and.isConstant());
		Assert.assertEquals(and.getClass(), AndRealtimeBoolean.class);

		if (and instanceof AndRealtimeBoolean) {
			AndRealtimeBoolean andBoolean = (AndRealtimeBoolean) and;
			List<RealtimeBoolean> list = andBoolean.getInnerValues();

			Assert.assertEquals(2, list.size());
		}
	}

	@Test
	public void testCreateAndWithOnlyEqualValues() throws RealtimeValueReadException {
		RealtimeBoolean and = RealtimeBoolean.createAnd(falseValue, falseValue, falseValue, falseValue);

		Assert.assertFalse(and.isConstant());
		Assert.assertEquals(and, falseValue);
	}

	@Test
	public void testCreateAndWithDifferentValuesAndConstantFalse() throws RealtimeValueReadException {
		RealtimeBoolean and = RealtimeBoolean.createAnd(falseValue, trueValue, createConstant(false),
				createValue(false));

		Assert.assertFalse(getValue(and));
	}

	@Test
	public void testCreateAndWithMultipleArgumentsEvaluatesCorrectly() throws RealtimeValueReadException {
		RealtimeBoolean true1 = RealtimeBoolean.TRUE;
		RealtimeBoolean true2 = RealtimeBoolean.TRUE;
		RealtimeBoolean true3 = RealtimeBoolean.TRUE;

		RealtimeBoolean false1 = RealtimeBoolean.FALSE;

		Assert.assertTrue(RealtimeBoolean.createAnd(true1, true2).getCurrentValue());
		Assert.assertTrue(RealtimeBoolean.createAnd(true1, true2, true3).getCurrentValue());
		Assert.assertFalse(RealtimeBoolean.createAnd(true1, false1).getCurrentValue());
		Assert.assertFalse(RealtimeBoolean.createAnd(true1, true2, false1).getCurrentValue());
	}

	@Test
	public void testCreateOrEvaluatesCorrectly() throws RealtimeValueReadException {
		RealtimeBoolean b = RealtimeBoolean.createOr(trueConstant, trueConstant, falseConstant);
		Assert.assertTrue(b.getCurrentValue());

		b = RealtimeBoolean.createOr(falseConstant, falseConstant, falseConstant);
		Assert.assertFalse(b.getCurrentValue());

		b = RealtimeBoolean.createOr(falseConstant, falseConstant, trueConstant);
		Assert.assertTrue(b.getCurrentValue());
	}

	@Test
	public void testCreateConditionalEvaluatesCorrectly() throws RealtimeValueReadException {
		WritableRealtimeBoolean value = createValue(true);
		RealtimeBoolean cond = RealtimeBoolean.createConditional(value, trueConstant, falseConstant);

		Assert.assertTrue(cond.getCurrentValue());
		value.setValue(false);
		Assert.assertFalse(cond.getCurrentValue());
	}

	@Test
	public void testCreateFromValueHasExpectedStructure() {
		RealtimeBoolean c = RealtimeBoolean.FALSE;

		Assert.assertTrue(c instanceof ConstantRealtimeBoolean);

		ConstantRealtimeBoolean cons = (ConstantRealtimeBoolean) c;

		Assert.assertFalse(cons.getValue());
	}

	@Test
	public void testCreateFromValueEvaluatesCorrectly() throws RealtimeValueReadException {
		Assert.assertTrue(RealtimeBoolean.TRUE.getCurrentValue());
		Assert.assertFalse(RealtimeBoolean.FALSE.getCurrentValue());
	}

	@Test
	public void testCreateFlipFlopHasExpectedStructure() {
		RealtimeBoolean b = RealtimeBoolean.createFlipFlop(falseConstant, trueConstant, mock(RoboticsRuntime.class));

		Assert.assertTrue(b instanceof FlipFlopRealtimeBoolean);

		FlipFlopRealtimeBoolean ff = (FlipFlopRealtimeBoolean) b;

		Assert.assertEquals(falseConstant, ff.getActivatingValue());
		Assert.assertEquals(trueConstant, ff.getDeactivatingValue());
		Assert.assertTrue(ff.isOneShot());
	}

	@Test
	public void testCreateFlipFlopOneShotHasExpectedStructure() {
		RealtimeBoolean b = RealtimeBoolean.createFlipFlop(falseConstant, trueConstant, true,
				mock(RoboticsRuntime.class));

		Assert.assertTrue(b instanceof FlipFlopRealtimeBoolean);

		FlipFlopRealtimeBoolean ff = (FlipFlopRealtimeBoolean) b;

		Assert.assertEquals(falseConstant, ff.getActivatingValue());
		Assert.assertEquals(trueConstant, ff.getDeactivatingValue());
		Assert.assertTrue(ff.isOneShot());
	}

	@Test
	public void testCreateWritableHasExpectedStructure() {
		WritableRealtimeBoolean createWritable = RealtimeBoolean.createWritable(true);

		Assert.assertTrue(createWritable instanceof WritableRealtimeBoolean);

		Assert.assertTrue(createWritable.getCheapValue());
	}

	@Test
	public void testCreateWritableEvaluatesCorrectly() throws RealtimeValueReadException {
		WritableRealtimeBoolean createWritable = RealtimeBoolean.createWritable(true);

		Assert.assertTrue(createWritable.getCurrentValue());

		createWritable.setValue(false);

		Assert.assertFalse(createWritable.getCurrentValue());
	}

	@Test
	public void testCreateGreaterHasExpectedStructure() {
		RealtimeDouble left = RealtimeDouble.createFromConstant(1);
		RealtimeDouble right = RealtimeDouble.createFromConstant(2);
		RealtimeBoolean greater = RealtimeBoolean.createGreater(left, right);

		Assert.assertTrue(greater instanceof GreaterDoubleRealtimeComparator);

		Assert.assertEquals(left, ((GreaterDoubleRealtimeComparator) greater).getLeft());
		Assert.assertEquals(right, ((GreaterDoubleRealtimeComparator) greater).getRight());
	}

	@Test
	public void testCreateGreaterEvaluatesCorrectly() throws RealtimeValueReadException {
		Assert.assertTrue(RealtimeBoolean
				.createGreater(RealtimeDouble.createFromConstant(2), RealtimeDouble.createFromConstant(1))
				.getCurrentValue());
		Assert.assertFalse(RealtimeBoolean
				.createGreater(RealtimeDouble.createFromConstant(1), RealtimeDouble.createFromConstant(2))
				.getCurrentValue());
	}

}
