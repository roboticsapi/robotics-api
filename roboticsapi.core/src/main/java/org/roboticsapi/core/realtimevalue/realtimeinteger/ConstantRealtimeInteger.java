/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * This class represents a constant {@link Integer} value available in real-time
 * in an execution environment.
 *
 * @see RealtimeValue
 * @see RealtimeInteger
 * @see RoboticsRuntime
 */
public final class ConstantRealtimeInteger extends RealtimeInteger {

	/**
	 * The constant integer value
	 */
	private final int value;

	/**
	 * Constructs a new constant {@link RealtimeInteger}.
	 *
	 * @param value integer value which will be constant over time
	 */
	ConstantRealtimeInteger(int value) {
		this.value = value;
	}

	/**
	 * Return the constant integer value
	 *
	 * @return the constant value
	 */
	public Integer getValue() {
		return value;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	protected Integer calculateCheapValue() {
		return getValue();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value == ((ConstantRealtimeInteger) obj).value;
	}

	@Override
	public int hashCode() {
		return classHash(value);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "" + value;
	}

	@Override
	public RealtimeInteger add(int other) {
		return CONSTANT_FOLDING ? createFromConstant(value + other) : super.add(other);
	}

	@Override
	public RealtimeInteger add(RealtimeInteger other) {
		if (CONSTANT_FOLDING && value == 0) {
			return other;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return add(other.getCheapValue());
		} else {
			return super.add(other);
		}
	}

	@Override
	public RealtimeInteger minus(int other) {
		return CONSTANT_FOLDING ? createFromConstant(value - other) : super.minus(other);
	}

	@Override
	public RealtimeInteger minus(RealtimeInteger other) {
		if (CONSTANT_FOLDING && value == 0) {
			return other;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return minus(other.getCheapValue());
		} else {
			return super.minus(other);
		}
	}

	@Override
	public RealtimeInteger multiply(int other) {
		return CONSTANT_FOLDING ? createFromConstant(value * other) : super.multiply(other);
	}

	@Override
	public RealtimeInteger multiply(RealtimeInteger other) {
		if (CONSTANT_FOLDING && value == 0) {
			return this;
		} else if (CONSTANT_FOLDING && value == 1) {
			return other;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return multiply(other.getCheapValue());
		} else {
			return super.multiply(other);
		}
	}

	@Override
	public RealtimeInteger negate() {
		return CONSTANT_FOLDING ? createFromConstant(-value) : super.negate();
	}

	@Override
	public RealtimeInteger square() {
		return CONSTANT_FOLDING ? createFromConstant(value * value) : super.square();
	}

	@Override
	public RealtimeBoolean isNull() {
		return CONSTANT_FOLDING ? RealtimeBoolean.FALSE : super.isNull();
	}

}
