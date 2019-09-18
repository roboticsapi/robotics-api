/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.Map;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

/**
 * This class represents a (time-variable) double value available in real-time
 * in an execution environment ({@link RealtimeValue} for {@link Double}).
 *
 * @see RealtimeValue
 * @see RoboticsRuntime
 */
public abstract class RealtimeDouble extends RealtimeValue<Double> {

	public static final RealtimeDouble ZERO = new ConstantRealtimeDouble(0);
	public static final RealtimeDouble ONE = new ConstantRealtimeDouble(1);

	@Override
	public final RealtimeDouble substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeDouble) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	/**
	 * Constructs a new {@link RealtimeDouble} composed of other
	 * {@link RealtimeValue}s.
	 *
	 * @param values other {@link RealtimeValue}s
	 */
	public RealtimeDouble(RealtimeValue<?>... values) {
		super(values);
	}

	/**
	 * Constructs a new {@link RealtimeDouble}.
	 */
	protected RealtimeDouble() {
		super();
	}

	/**
	 * Constructs a new {@link RealtimeDouble} with a {@link Command} as scope.
	 *
	 * @param scope the command in which this object is valid.
	 */
	public RealtimeDouble(Command scope) {
		super(scope);
	}

	/**
	 * Constructs a new {@link RealtimeDouble} with a {@link RoboticsRuntime}.
	 *
	 * @param runtime the runtime in which this object is valid
	 */
	public RealtimeDouble(RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Returns a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 * this object is greater than the other {@link RealtimeDouble}.
	 *
	 * @param value the other {@link RealtimeDouble}
	 * @return a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 *         this object is greater than the other {@link RealtimeDouble}; to
	 *         <code>false</code> otherwise.
	 */
	public RealtimeBoolean greater(RealtimeDouble value) {
		return RealtimeBoolean.createGreater(this, value);
	}

	/**
	 * Returns a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 * this object is greater than the specified double value.
	 *
	 * @param value the double value
	 * @return a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 *         this object is greater than the specified double value; to
	 *         <code>false</code> otherwise.
	 */
	public RealtimeBoolean greater(double value) {
		return greater(RealtimeDouble.createFromConstant(value));
	}

	/**
	 * Returns a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 * this object is less than the other {@link RealtimeDouble}.
	 *
	 * @param value the other {@link RealtimeDouble}
	 * @return a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 *         this object is less than the other {@link RealtimeDouble}; to
	 *         <code>false</code> otherwise.
	 */
	public RealtimeBoolean less(RealtimeDouble value) {
		return RealtimeBoolean.createGreater(value, this);
	}

	/**
	 * Returns a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 * this object is less than the specified double value.
	 *
	 * @param value the double value
	 * @return a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 *         this object is less than the specified double value; to
	 *         <code>false</code> otherwise.
	 */
	public RealtimeBoolean less(double value) {
		return less(RealtimeDouble.createFromConstant(value));
	}

	/**
	 * Returns a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 * this object can be considered equal to the other {@link RealtimeDouble},
	 * i.e., it tests if the absolute difference between the two
	 * {@link RealtimeDouble}s has a difference less then a given epsilon.
	 *
	 * @param value   the other {@link RealtimeDouble}
	 * @param epsilon The precision of the test
	 * @return a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 *         this object can be considered equal to the other
	 *         {@link RealtimeDouble}; to <code>false</code> otherwise.
	 */
	public RealtimeBoolean equals(RealtimeDouble value, double epsilon) {
		return add(epsilon).greater(value).and(add(-epsilon).less(value));
	}

	/**
	 * Returns a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 * this object can be considered equal to the specified double value, i.e., it
	 * tests if the absolute difference between the two values has a difference less
	 * then a given epsilon.
	 *
	 * @param value   the double value
	 * @param epsilon The precision of the test
	 * @return a {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 *         this object can be considered equal to the double value; to
	 *         <code>false</code> otherwise.
	 */
	public RealtimeBoolean equals(double value, double epsilon) {
		return equals(RealtimeDouble.createFromConstant(value), epsilon);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeDoubleIsNull(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the negated value of this
	 * {@link RealtimeDouble}.
	 *
	 * @return {@code -this}
	 */
	public RealtimeDouble negate() {
		return new NegatedRealtimeDouble(this);
	}

	/**
	 * Returns a new {@code RealtimeDouble} whose value is {@code (this + other)}.
	 *
	 * @param other value to be added to this {@code RealtimeDouble}.
	 * @return {@code this + other}
	 */
	public RealtimeDouble add(RealtimeDouble other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.add(this);
		}
		return new AddedRealtimeDouble(this, other);
	}

	/**
	 * Returns a new {@code RealtimeDouble} whose value is {@code (this + other)}.
	 *
	 * @param other value to be added to this {@code RealtimeDouble}.
	 * @return {@code this + other}
	 */
	public RealtimeDouble add(double other) {
		return add(RealtimeDouble.createFromConstant(other));
	}

	/**
	 * Returns a {@code RealtimeDouble} whose value is {@code (this - other)}.
	 *
	 * @param other value to be subtracted from to this {@code RealtimeDouble}.
	 * @return {@code this - other}
	 */
	public RealtimeDouble minus(RealtimeDouble other) {
		if (CONSTANT_FOLDING && this.equals(other)) {
			return createFromConstant(0);
		}
		return add(other.negate());
	}

	/**
	 * Returns a {@code RealtimeDouble} whose value is {@code (this - other)}.
	 *
	 * @param other value to be subtracted from to this {@code RealtimeDouble}.
	 * @return {@code this - other}
	 */
	public RealtimeDouble minus(double other) {
		return add(-other);
	}

	/**
	 * Returns a {@code RealtimeDouble} whose value is {@code (this * other)}.
	 *
	 * @param other value to be multiplied with this {@code RealtimeDouble}.
	 * @return {@code this * other}
	 */
	public RealtimeDouble multiply(RealtimeDouble other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.multiply(this);
		}
		return new MultipliedRealtimeDouble(this, other);
	}

	/**
	 * Returns a {@code RealtimeDouble} whose value is {@code (this * other)}.
	 *
	 * @param other value to be multiplied with this {@code RealtimeDouble}.
	 * @return {@code this * other}
	 */
	public RealtimeDouble multiply(double other) {
		return multiply(RealtimeDouble.createFromConstant(other));
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the squared value of this
	 * {@link RealtimeDouble}.
	 *
	 * @return {@code this * this}.
	 */
	public RealtimeDouble square() {
		return multiply(this);
	}

	/**
	 * Returns a {@code RealtimeDouble} whose value is {@code (this / other)}.
	 *
	 * @param other value by which this {@code RealtimeDouble} is to be divided.
	 * @return {@code this / other}
	 */
	public RealtimeDouble divide(double other) {
		return divide(RealtimeDouble.createFromConstant(other));
	}

	/**
	 * Returns a {@code RealtimeDouble} whose value is {@code (this / other)}.
	 *
	 * @param other value by which this {@code RealtimeDouble} is to be divided.
	 * @return {@code this / other}
	 */
	public RealtimeDouble divide(RealtimeDouble other) {
		if (CONSTANT_FOLDING && other.isConstant() && other.getCheapValue() == 1) {
			return this;
		}
		return new DividedRealtimeDouble(this, other);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the square root of this
	 * {@link RealtimeDouble}.
	 *
	 * @return the square root.
	 */
	public RealtimeDouble sqrt() {
		return new SquareRootRealtimeDouble(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the absolute value of this
	 * {@link RealtimeDouble}.
	 *
	 * @return the absolute value.
	 */
	public RealtimeDouble abs() {
		return new AbsRealtimeDouble(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the trigonometric sine of
	 * this {@link RealtimeDouble}.
	 *
	 * @return the trigonometric sine.
	 */
	public RealtimeDouble sin() {
		return new SinRealtimeDouble(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the trigonometric cosine of
	 * this {@link RealtimeDouble}.
	 *
	 * @return the trigonometric cosine.
	 */
	public RealtimeDouble cos() {
		return new CosRealtimeDouble(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the trigonometric tangent
	 * of this {@link RealtimeDouble}.
	 *
	 * @return the trigonometric tangent.
	 */
	public RealtimeDouble tan() {
		return new TanRealtimeDouble(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the arc sine of this
	 * {@link RealtimeDouble}.
	 *
	 * @return the arc sine.
	 */
	public RealtimeDouble asin() {
		return new AsinRealtimeDouble(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the arc cosine of this
	 * {@link RealtimeDouble}.
	 *
	 * @return the arc cosine.
	 */
	public RealtimeDouble acos() {
		return new AcosRealtimeDouble(this);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing an exponential smoothing of
	 * this {@link RealtimeDouble} for the given <code>halfLife</code>.
	 *
	 * @param halfLife the half life
	 * @return the exponential smoothing for the given <code>halfLife</code>.
	 */
	public RealtimeDouble exponentialSmoothing(double halfLife) {
		return new ExponentiallySmootedRealtimeDouble(this, halfLife);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing a sliding average of this
	 * {@link RealtimeDouble} for the given <code>duration</code>.
	 *
	 * @param duration the duration
	 * @return the sliding average for the given <code>duration</code>.
	 */
	public RealtimeDouble slidingAverage(double duration) {
		return new SlidingAverageRealtimeDouble(this, duration);
	}

	public RealtimeDouble limit(double lowerBound, double upperBound) {
		return limit(createFromConstant(lowerBound), createFromConstant(upperBound));
	}

	public RealtimeDouble limit(RealtimeDouble lowerBound, RealtimeDouble upperBound) {
		RealtimeDouble ret = this;

		if (lowerBound != null) {
			ret = ret.lowerLimit(lowerBound);
		}
		if (upperBound != null) {
			ret = ret.upperLimit(upperBound);
		}
		return ret;
	}

	public RealtimeDouble lowerLimit(double bound) {
		return lowerLimit(createFromConstant(bound));
	}

	public RealtimeDouble lowerLimit(RealtimeDouble bound) {
		return createConditional(this.greater(bound), this, bound);
	}

	public RealtimeDouble upperLimit(double bound) {
		return upperLimit(createFromConstant(bound));
	}

	public RealtimeDouble upperLimit(RealtimeDouble bound) {
		return createConditional(this.less(bound), this, bound);
	}

	@Override
	public RealtimeDouble fromHistory(RealtimeDouble age, double maxAge) {
		// FIXME: implement
		return null;
	}

	/**
	 * Computes the biggest value of multiple {@link RealtimeDouble}s.
	 *
	 * @param value   the first value to check
	 * @param further further values to check
	 * @return a {@link RealtimeDouble} representing the maximum
	 */
	public static RealtimeDouble createMax(RealtimeDouble value, RealtimeDouble... further) {
		RealtimeDouble ret = value;

		for (RealtimeDouble v : further) {
			ret = ret.lowerLimit(v);
		}
		return ret;
	}

	/**
	 * Computes the biggest value of multiple {@link RealtimeDouble}s. Furthermore,
	 * a constant double value is considered which serves as an lower bound.
	 *
	 * @param lowerBound the lower bound
	 * @param values     the values to check
	 * @return a {@link RealtimeDouble} representing the maximum.
	 */
	public static RealtimeDouble createMax(double lowerBound, RealtimeDouble... values) {
		return createMax(createFromConstant(lowerBound), values);
	}

	/**
	 * Computes the smallest value of multiple {@link RealtimeDouble}s.
	 *
	 * @param value   the first value to check
	 * @param further further values to check
	 * @return a {@link RealtimeDouble} representing the minimum
	 */
	public static RealtimeDouble createMin(RealtimeDouble value, RealtimeDouble... further) {
		RealtimeDouble ret = value;

		for (RealtimeDouble v : further) {
			ret = ret.upperLimit(v);
		}
		return ret;
	}

	/**
	 * Computes the smallest value of multiple {@link RealtimeDouble}s. Furthermore,
	 * a constant double value is considered which serves as an upper bound.
	 *
	 * @param upperBound the upper bound
	 * @param values     the values to check
	 * @return a {@link RealtimeDouble} representing the minimum.
	 */
	public static RealtimeDouble createMin(double upperBound, RealtimeDouble... values) {
		return createMin(createFromConstant(upperBound), values);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing the angle <i>theta</i> from
	 * the conversion of rectangular coordinates ({@code x},&nbsp;{@code y}) to
	 * polar coordinates (r,&nbsp;<i>theta</i>). This method computes the phase
	 * <i>theta</i> by computing an arc tangent of {@code y/x} in the range of
	 * -<i>pi</i> to <i>pi</i>.
	 *
	 * @param y the ordinate coordinate
	 * @param x the abscissa coordinate
	 * @return the <i>theta</i> component of the point (<i>r</i>,&nbsp;<i>theta</i>)
	 *         in polar coordinates that corresponds to the point
	 *         (<i>x</i>,&nbsp;<i>y</i>) in Cartesian coordinates.
	 */
	public static RealtimeDouble createAtan2(RealtimeDouble y, RealtimeDouble x) {
		if (CONSTANT_FOLDING && x.isConstant() && y.isConstant()) {
			return RealtimeDouble.createFromConstant(Math.atan2(y.getCheapValue(), x.getCheapValue()));
		}
		return new Atan2RealtimeDouble(y, x);
	}

	/**
	 * Returns a new {@link RealtimeDouble} representing a conditional of two
	 * {@link RealtimeDouble}s.
	 *
	 * @param condition a {@link RealtimeBoolean} as condition.
	 * @param ifTrue    the {@link RealtimeDouble} if the condition evaluates to
	 *                  <code>true</code>.
	 * @param ifFalse   the {@link RealtimeDouble} if the condition evaluates to
	 *                  <code>false</code>.
	 * @return the conditional value.
	 */
	public static RealtimeDouble createConditional(RealtimeBoolean condition, RealtimeDouble ifTrue,
			RealtimeDouble ifFalse) {
		if (CONSTANT_FOLDING && condition.isConstant()) {
			return condition.getCheapValue() ? ifTrue : ifFalse;
		}
		return new ConditionalRealtimeDouble(condition, ifTrue, ifFalse);
	}

	public static RealtimeDouble createConditional(RealtimeBoolean condition, RealtimeDouble ifTrue, double ifFalse) {
		return createConditional(condition, ifTrue, createFromConstant(ifFalse));
	}

	public static RealtimeDouble createConditional(RealtimeBoolean condition, double ifTrue, RealtimeDouble ifFalse) {
		return createConditional(condition, createFromConstant(ifTrue), ifFalse);
	}

	public static RealtimeDouble createConditional(RealtimeBoolean condition, double ifTrue, double ifFalse) {
		return createConditional(condition, createFromConstant(ifTrue), createFromConstant(ifFalse));
	}

	/**
	 * Constructs a new {@link RealtimeDouble} representing the specified double
	 * value.
	 *
	 * @param value a double value
	 * @return a {@link RealtimeDouble} representing the specified double value.
	 */
	public static RealtimeDouble createFromConstant(double value) {
		if (value == 0) {
			return ZERO;
		} else if (value == 1) {
			return ONE;
		} else {
			return new ConstantRealtimeDouble(value);
		}
	}

	/**
	 * Constructs a new {@link RealtimeDouble} from the given
	 * {@link RealtimeInteger}.
	 *
	 * @param integer the integer realtime value
	 * @return RealtimeDouble representing the integer value converted to double
	 */
	public static RealtimeDouble createFromInteger(RealtimeValue<Integer> integer) {
		return new RealtimeIntegerToRealtimeDouble(integer);
	}

	public static RealtimeDouble createAbsolute(RealtimeDouble value) {
		return new AbsRealtimeDouble(value);
	}

	public static WritableRealtimeDouble createWritable(double defaultValue) {
		return new WritableRealtimeDouble(defaultValue);
	}

	public static WritableRealtimeDoubleArray createWritableArray(Double[] defaultValues) {
		return new WritableRealtimeDoubleArray(defaultValues);
	}

}
