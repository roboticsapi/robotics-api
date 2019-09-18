/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import java.util.Map;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * This class represents a (time-variable) integer value available in real-time
 * in an execution environment ({@link RealtimeValue} for {@link Integer}).
 *
 * @see RealtimeValue
 * @see RoboticsRuntime
 */
public abstract class RealtimeInteger extends RealtimeValue<Integer> {
	public static final RealtimeInteger ZERO = new ConstantRealtimeInteger(0);
	public static final RealtimeInteger ONE = new ConstantRealtimeInteger(1);

	@Override
	public final RealtimeInteger substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeInteger) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeInteger performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	/**
	 * Constructs a new {@link RealtimeInteger} composed of other
	 * {@link RealtimeValue}s.
	 *
	 * @param values other {@link RealtimeValue}s
	 */
	public RealtimeInteger(RealtimeValue<?>... values) {
		super(values);
	}

	/**
	 * Constructs a new {@link RealtimeInteger}.
	 */
	public RealtimeInteger() {
		super();
	}

	/**
	 * Constructs a new {@link RealtimeInteger} with a {@link Command} as scope.
	 *
	 * @param scope the command in which this object is valid.
	 */
	public RealtimeInteger(Command scope) {
		super(scope);
	}

	/**
	 * Constructs a new {@link RealtimeInteger} with a {@link RoboticsRuntime}.
	 *
	 * @param runtime the runtime in which this object is valid
	 */
	public RealtimeInteger(RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Returns a new {@link RealtimeInteger} representing the negated value of this
	 * {@link RealtimeInteger}.
	 *
	 * @return {@code -this}
	 */
	public RealtimeInteger negate() {
		return new NegatedRealtimeInteger(this);
	}

	/**
	 * Returns a new {@code RealtimeInteger} whose value is {@code (this + other)}.
	 *
	 * @param other value to be added to this {@code RealtimeInteger}.
	 * @return {@code this + other}
	 */
	public RealtimeInteger add(RealtimeInteger other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.add(this);
		}
		return new AddedRealtimeInteger(this, other);
	}

	/**
	 * Returns a new {@code RealtimeInteger} whose value is {@code (this + other)}.
	 *
	 * @param other value to be added to this {@code RealtimeInteger}.
	 * @return {@code this + other}
	 */
	public RealtimeInteger add(int other) {
		return add(RealtimeInteger.createFromConstant(other));
	}

	/**
	 * Returns a {@code RealtimeInteger} whose value is {@code (this - other)}.
	 *
	 * @param other value to be subtracted from to this {@code RealtimeInteger}.
	 * @return {@code this - other}
	 */
	public RealtimeInteger minus(RealtimeInteger other) {
		if (CONSTANT_FOLDING && this.equals(other)) {
			return createFromConstant(0);
		}
		return add(other.negate());
	}

	/**
	 * Returns a {@code RealtimeInteger} whose value is {@code (this - other)}.
	 *
	 * @param other value to be subtracted from to this {@code RealtimeInteger}.
	 * @return {@code this - other}
	 */
	public RealtimeInteger minus(int other) {
		return add(-other);
	}

	/**
	 * Returns a {@code RealtimeInteger} whose value is {@code (this * other)}.
	 *
	 * @param other value to be multiplied with this {@code RealtimeInteger}.
	 * @return {@code this * other}
	 */
	public RealtimeInteger multiply(RealtimeInteger other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.multiply(this);
		}
		return new MultipliedRealtimeInteger(this, other);
	}

	/**
	 * Returns a {@code RealtimeInteger} whose value is {@code (this * other)}.
	 *
	 * @param other value to be multiplied with this {@code RealtimeInteger}.
	 * @return {@code this * other}
	 */
	public RealtimeInteger multiply(int other) {
		return multiply(RealtimeInteger.createFromConstant(other));
	}

	public RealtimeInteger divide(RealtimeInteger divisor) {
		return new DividedRealtimeInteger(this, divisor);
	}

	/**
	 * Returns a new {@link RealtimeInteger} representing the squared value of this
	 * {@link RealtimeInteger}.
	 *
	 * @return {@code this * this}.
	 */
	public RealtimeInteger square() {
		return multiply(this);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeIntegerIsNull(this);
	}

	@Override
	public RealtimeInteger fromHistory(RealtimeDouble age, double maxAge) {
		// FIXME: implement
		return null;
	}

	/**
	 * Constructs a new {@link RealtimeInteger} representing the specified double
	 * value.
	 *
	 * @param value a integer value
	 * @return a {@link RealtimeInteger} representing the specified integer value.
	 */
	public static RealtimeInteger createFromConstant(int value) {
		if (value == 0) {
			return ZERO;
		} else if (value == 1) {
			return ONE;
		} else {
			return new ConstantRealtimeInteger(value);
		}
	}

	/**
	 * Returns a new {@link RealtimeInteger} representing a conditional of two
	 * {@link RealtimeInteger}s.
	 *
	 * @param condition a {@link RealtimeBoolean} as condition.
	 * @param ifTrue    the {@link RealtimeInteger} if the condition evaluates to
	 *                  <code>true</code>.
	 * @param ifFalse   the {@link RealtimeInteger} if the condition evaluates to
	 *                  <code>false</code>.
	 * @return the conditional value.
	 */
	public static RealtimeInteger createConditional(RealtimeBoolean condition, RealtimeInteger ifTrue,
			RealtimeInteger ifFalse) {
		if (CONSTANT_FOLDING && condition.isConstant()) {
			return condition.getCheapValue() ? ifTrue : ifFalse;
		}
		return new ConditionalRealtimeInteger(condition, ifTrue, ifFalse);
	}

	public static WritableRealtimeInteger createWritable(int defaultValue) {
		return new WritableRealtimeInteger(defaultValue);
	}

	public AbsRealtimeInteger abs() {
		return new AbsRealtimeInteger(this);
	}

	public RealtimeBoolean isEqual(RealtimeInteger other) {
		return new EqualRealtimeInteger(this, other);
	}

	public RealtimeBoolean isEqual(Integer other) {
		return new EqualRealtimeInteger(this, createFromConstant(other));
	}

}
