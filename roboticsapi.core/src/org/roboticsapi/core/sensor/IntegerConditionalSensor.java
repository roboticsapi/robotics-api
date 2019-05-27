/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link DerivedIntegerSensor} that evaluates a
 * condition and passes on the appropriate of both inner {@link IntegerSensor}s.
 */
public final class IntegerConditionalSensor extends DerivedIntegerSensor {

	/**
	 * Constructs a new {@link IntegerConditionalSensor}.
	 * 
	 * @param condition the condition to evaluate.
	 * @param ifTrue    sensor passed on if condition evaluates to
	 *                  <code>true</code>.
	 * @param ifFalse   sensor passed on if condition evaluates to
	 *                  <code>false</code>.
	 */
	public IntegerConditionalSensor(BooleanSensor condition, IntegerSensor ifTrue, IntegerSensor ifFalse) {
		super(condition, ifTrue, ifFalse);
	}

	/**
	 * Constructs a new {@link IntegerConditionalSensor}.
	 * 
	 * @param condition the condition to evaluate.
	 * @param ifTrue    sensor passed on if condition evaluates to
	 *                  <code>true</code>.
	 * @param ifFalse   value passed on if condition evaluates to
	 *                  <code>false</code>.
	 */
	public IntegerConditionalSensor(BooleanSensor condition, IntegerSensor ifTrue, int ifFalse) {
		this(condition, ifTrue, new ConstantIntegerSensor(ifFalse));
	}

	/**
	 * Constructs a new {@link IntegerConditionalSensor}.
	 * 
	 * @param condition the condition to evaluate.
	 * @param ifTrue    value passed on if condition evaluates to <code>true</code>.
	 * @param ifFalse   sensor passed on if condition evaluates to
	 *                  <code>false</code>.
	 */

	public IntegerConditionalSensor(BooleanSensor condition, int ifTrue, IntegerSensor ifFalse) {
		this(condition, new ConstantIntegerSensor(ifTrue), ifFalse);
	}

	/**
	 * Constructs a new {@link IntegerConditionalSensor}.
	 * 
	 * @param condition the condition to evaluate.
	 * @param ifTrue    value passed on if condition evaluates to <code>true</code>.
	 * @param ifFalse   value passed on if condition evaluates to
	 *                  <code>false</code>.
	 */
	public IntegerConditionalSensor(BooleanSensor condition, int ifTrue, int ifFalse) {
		this(condition, ifTrue, new ConstantIntegerSensor(ifFalse));
	}

	/**
	 * Returns the {@link BooleanSensor} holding the condition.
	 * 
	 * @return the condition.
	 */
	public BooleanSensor getCondition() {
		return (BooleanSensor) getInnerSensor(0);
	}

	/**
	 * Returns the {@link IntegerSensor} that is passed on if condition evaluates to
	 * <code>true</code>.
	 * 
	 * @return the sensor if condition evaluates to <code>true</code>.
	 */
	public IntegerSensor getIfTrue() {
		return (IntegerSensor) getInnerSensor(1);
	}

	/**
	 * Returns the {@link IntegerSensor} that is passed on if condition evaluates to
	 * <code>false</code>.
	 * 
	 * @return the sensor if condition evaluates to <code>false</code>.
	 */
	public IntegerSensor getIfFalse() {
		return (IntegerSensor) getInnerSensor(2);
	}

	@Override
	public String toString() {
		return "(" + getCondition() + " ? " + getIfTrue() + " : " + getIfFalse() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return (Integer) (((Boolean) values[0]) ? values[1] : values[2]);
	}
}
