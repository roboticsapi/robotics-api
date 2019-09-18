/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * This class represents a constant {@link Double} value available in real-time
 * in an execution environment.
 *
 * @see RealtimeValue
 * @see RealtimeDouble
 * @see RoboticsRuntime
 */
public final class ConstantRealtimeDouble extends RealtimeDouble {

	/**
	 * The constant double value
	 */
	private final double value;

	/**
	 * Constructs a new constant {@link RealtimeDouble}.
	 *
	 * @param value double value which will be constant over time
	 */
	ConstantRealtimeDouble(double value) {
		this.value = value;
	}

	/**
	 * Returns the constant double value
	 *
	 * @return the constant value
	 */
	public double getValue() {
		return value;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	protected Double calculateCheapValue() {
		return getValue();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value == ((ConstantRealtimeDouble) obj).value;
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
	public RealtimeDouble abs() {
		return CONSTANT_FOLDING ? createFromConstant(Math.abs(value)) : super.abs();
	}

	@Override
	public RealtimeDouble negate() {
		return CONSTANT_FOLDING ? createFromConstant(-value) : super.negate();
	}

	@Override
	public RealtimeDouble add(double other) {
		return CONSTANT_FOLDING ? createFromConstant(value + other) : super.add(other);
	}

	@Override
	public RealtimeDouble add(RealtimeDouble other) {
		if (CONSTANT_FOLDING && value == 0) {
			return other;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return add(other.getCheapValue());
		} else {
			return super.add(other);
		}
	}

	@Override
	public RealtimeDouble minus(double other) {
		return CONSTANT_FOLDING ? createFromConstant(value - other) : super.minus(other);
	}

	@Override
	public RealtimeDouble minus(RealtimeDouble other) {
		if (CONSTANT_FOLDING && value == 0) {
			return other;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return minus(other.getCheapValue());
		} else {
			return super.minus(other);
		}
	}

	@Override
	public RealtimeDouble multiply(double other) {
		return CONSTANT_FOLDING ? createFromConstant(value * other) : super.multiply(other);
	}

	@Override
	public RealtimeDouble multiply(RealtimeDouble other) {
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
	public RealtimeDouble divide(double other) {
		return CONSTANT_FOLDING ? createFromConstant(value / other) : super.divide(other);
	}

	@Override
	public RealtimeDouble divide(RealtimeDouble other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return divide(other.getCheapValue());
		} else {
			return super.divide(other);
		}
	}

	@Override
	public RealtimeBoolean greater(double value) {
		return CONSTANT_FOLDING ? RealtimeBoolean.createFromConstant(this.value > value) : super.greater(value);
	}

	@Override
	public RealtimeBoolean greater(RealtimeDouble value) {
		if (CONSTANT_FOLDING && value.isConstant()) {
			return greater(value.getCheapValue());
		} else {
			return super.greater(value);
		}
	}

	@Override
	public RealtimeBoolean less(double value) {
		return CONSTANT_FOLDING ? RealtimeBoolean.createFromConstant(this.value < value) : super.less(value);
	}

	@Override
	public RealtimeBoolean less(RealtimeDouble value) {
		if (CONSTANT_FOLDING && value.isConstant()) {
			return less(value.getCheapValue());
		} else {
			return super.less(value);
		}
	}

	@Override
	public RealtimeDouble exponentialSmoothing(double halfLife) {
		return CONSTANT_FOLDING ? this : super.exponentialSmoothing(halfLife);
	}

	@Override
	public RealtimeDouble slidingAverage(double duration) {
		return CONSTANT_FOLDING ? this : super.slidingAverage(duration);
	}

	@Override
	public RealtimeDouble sqrt() {
		return CONSTANT_FOLDING ? createFromConstant(Math.sqrt(value)) : super.sqrt();
	}

	@Override
	public RealtimeDouble square() {
		return CONSTANT_FOLDING ? createFromConstant(value * value) : super.square();
	}

	@Override
	public RealtimeDouble sin() {
		return CONSTANT_FOLDING ? createFromConstant(Math.sin(value)) : super.sin();
	}

	@Override
	public RealtimeDouble cos() {
		return CONSTANT_FOLDING ? createFromConstant(Math.cos(value)) : super.cos();
	}

	@Override
	public RealtimeDouble tan() {
		return CONSTANT_FOLDING ? createFromConstant(Math.tan(value)) : super.tan();
	}

	@Override
	public RealtimeDouble asin() {
		return CONSTANT_FOLDING ? createFromConstant(Math.asin(value)) : super.asin();
	}

	@Override
	public RealtimeDouble acos() {
		return CONSTANT_FOLDING ? createFromConstant(Math.acos(value)) : super.acos();
	}

	@Override
	public RealtimeBoolean isNull() {
		return CONSTANT_FOLDING ? RealtimeBoolean.FALSE : super.isNull();
	}

}
