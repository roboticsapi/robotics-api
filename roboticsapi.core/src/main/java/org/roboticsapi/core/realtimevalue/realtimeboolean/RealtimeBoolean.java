/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import java.util.Arrays;
import java.util.Map;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.runtime.CommandRealtimeException;

/**
 * This class represents a (time-variable) Boolean value available in real-time
 * in an execution environment ({@link RealtimeValue} for {@link Boolean}).
 *
 * @see RealtimeValue
 * @see RoboticsRuntime
 */
public abstract class RealtimeBoolean extends RealtimeValue<Boolean> {
	public static final RealtimeBoolean TRUE = new ConstantRealtimeBoolean(true);
	public static final RealtimeBoolean FALSE = new ConstantRealtimeBoolean(false);

	@Override
	public final RealtimeBoolean substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeBoolean) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeBoolean performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	/**
	 * Constructs a new {@link RealtimeBoolean} composed of other
	 * {@link RealtimeValue}s.
	 *
	 * @param values other {@link RealtimeValue}s
	 */
	public RealtimeBoolean(RealtimeValue<?>... values) {
		super(values);
	}

	/**
	 * Constructs a new {@link RealtimeBoolean}.
	 */
	public RealtimeBoolean() {
		super();
	}

	/**
	 * Constructs a new {@link RealtimeBoolean} with a {@link Command} as scope.
	 *
	 * @param scope the command in which this object is valid.
	 */
	public RealtimeBoolean(Command scope) {
		super(scope);
	}

	/**
	 * Constructs a new {@link RealtimeBoolean} with a {@link RoboticsRuntime}.
	 *
	 * @param runtime the runtime in which this object is valid
	 */
	public RealtimeBoolean(RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Returns a new {@link RealtimeBoolean} representing the logical negation of
	 * this object.
	 *
	 * @return {@code !this}
	 */
	public RealtimeBoolean not() {
		return new NegatedRealtimeBoolean(this);
	}

	@Override
	public RealtimeBoolean getForScope(Command command) {
		return this;
	}

	/**
	 * Returns a new {@link RealtimeBoolean} which represents the logical
	 * disjunction of this object with the other {@link RealtimeBoolean}.
	 *
	 * @param other the {@link RealtimeBoolean} to perform logical disjunction with
	 * @return {@code this | other}
	 */
	public RealtimeBoolean or(RealtimeBoolean other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.or(this);
		} else if (this.equals(other)) {
			return this;
		} else if (other instanceof OrRealtimeBoolean) {
			return other.or(this);
		} else {
			return new OrRealtimeBoolean(this).or(other);
		}
	}

	/**
	 * Returns a new {@link RealtimeBoolean} which represents the logical
	 * disjunction of this object with an array of other {@link RealtimeBoolean}s.
	 *
	 * @param others an array with others {@link RealtimeBoolean}s to perform the
	 *               logical disjunction with, can be empty
	 * @return {@code this | others[0] | ... }
	 */
	public final RealtimeBoolean or(RealtimeBoolean... others) {
		RealtimeBoolean ret = this;

		for (RealtimeBoolean other : others) {
			ret = ret.or(other);
		}
		return ret;
	}

	/**
	 * Returns a new {@link RealtimeBoolean} which represents the logical
	 * conjunction of this object with the other {@link RealtimeBoolean}.
	 *
	 * @param other the {@link RealtimeBoolean} to perform logical conjunction with
	 * @return {@code this & other}
	 */
	public RealtimeBoolean and(RealtimeBoolean other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.and(this);
		} else if (this.equals(other)) {
			return this;
		} else if (other instanceof AndRealtimeBoolean) {
			return other.and(this);
		} else {
			return new AndRealtimeBoolean(this).and(other);
		}
	}

	/**
	 * Returns a new {@link RealtimeBoolean} which represents the exclusive
	 * disjunction of this object with the other {@link RealtimeBoolean}.
	 *
	 * @param other the {@link RealtimeBoolean} to perform exclusive disjunction
	 *              with
	 * @return {@code this XOR other}
	 */
	public RealtimeBoolean xor(RealtimeBoolean other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.xor(this);
		}
		return (not().and(other)).or(and(other.not()));
	}

	/**
	 * Returns a new {@link RealtimeBoolean} which evaluates to <code>true</code> if
	 * this object has already been active.
	 *
	 * @return a {@link RealtimeBoolean} which indicates if this object has already
	 *         been active.
	 */
	public RealtimeBoolean hasBeenActive(RoboticsRuntime runtime) {
		return new FlipFlopRealtimeBoolean(this, FALSE, runtime);
	}

	@Override
	public RealtimeBoolean fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeBooleanAtTime(this, age, maxAge);
	}

	@Override
	public RealtimeBoolean fromHistory(double age) {
		return (RealtimeBoolean) super.fromHistory(age);
	}

	@Override
	public RealtimeBoolean isNull() {
		if (CONSTANT_FOLDING && this.isConstant()) {
			return RealtimeBoolean.createFromConstant(this.getCheapValue() == null);
		}
		return new RealtimeBooleanIsNull(this);
	}

	/**
	 * Constructs a new {@link RealtimeBoolean} representing the specified Boolean
	 * value.
	 *
	 * @param value a Boolean value
	 * @return a {@link RealtimeBoolean} representing the specified Boolean value.
	 */
	public static RealtimeBoolean createFromConstant(boolean value) {
		if (value) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	public static RealtimeBoolean createFromException(CommandRealtimeException exception) {
		return new ExceptionRealtimeBoolean(exception);
	}

	public static RealtimeBoolean createGreater(RealtimeDouble left, RealtimeDouble right) {
		return new GreaterDoubleRealtimeComparator(left, right);
	}

	/**
	 * Returns a new {@link RealtimeBoolean} representing a conditional of two
	 * {@link RealtimeBoolean}s.
	 *
	 * @param condition a {@link RealtimeBoolean} as condition.
	 * @param ifTrue    the {@link RealtimeBoolean} if the condition evaluates to
	 *                  <code>true</code>.
	 * @param ifFalse   the {@link RealtimeBoolean} if the condition evaluates to
	 *                  <code>false</code>.
	 * @return the conditional value.
	 */
	public static RealtimeBoolean createConditional(RealtimeBoolean condition, RealtimeBoolean ifTrue,
			RealtimeBoolean ifFalse) {
		if (CONSTANT_FOLDING && condition.isConstant()) {
			return condition.getCheapValue() ? ifTrue : ifFalse;
		}
		return new ConditionalRealtimeBoolean(condition, ifTrue, ifFalse);
	}

	/**
	 * Returns a new {@link RealtimeBoolean} which represents the logical
	 * conjunction of an array of {@link RealtimeBoolean}s.
	 *
	 * @param others an array with others {@link RealtimeBoolean}s to perform the
	 *               logical conjunction with
	 * @return {@code value1 & value2 & ... }
	 */
	public static RealtimeBoolean createAnd(RealtimeBoolean... values) {
		if (values == null) {
			throw new IllegalArgumentException("Argument may not be null");
		}
		if (values.length < 1) {
			throw new IllegalArgumentException("At least one value must be supplied");
		}

		RealtimeBoolean ret = values[0];

		for (RealtimeBoolean other : Arrays.copyOfRange(values, 1, values.length)) {
			ret = ret.and(other);
		}
		return ret;
	}

	/**
	 * Returns a new {@link RealtimeBoolean} which represents the logical
	 * disjunction of an array of {@link RealtimeBoolean}s.
	 *
	 * @param others an array with others {@link RealtimeBoolean}s to perform the
	 *               logical disjunction with
	 * @return {@code value1 || value2 || ... }
	 */
	public static RealtimeBoolean createOr(RealtimeBoolean... values) {
		if (values == null) {
			throw new IllegalArgumentException("Argument may not be null");
		}
		if (values.length < 1) {
			throw new IllegalArgumentException("At least one value must be supplied");
		}

		RealtimeBoolean ret = values[0];

		for (RealtimeBoolean other : Arrays.copyOfRange(values, 1, values.length)) {
			ret = ret.or(other);
		}
		return ret;
	}

	/**
	 * Creates a new RealtimeBoolean that is activated and deactivated by the
	 * supplied RealtimeBooleans.
	 *
	 * This RealtimeBoolean acts like a one-shot flip-flop, i.e., it is activated
	 * once when the activating RealtimeBoolean becomes true, and deactivated
	 * forever when the deactivating RealtimeBoolean becomes true.
	 *
	 * If both the activating and deactivating value are true at once, the created
	 * RealtimeBoolean is false (forever).
	 *
	 * At least one of [activated, deactivated] must have a RoboticsRuntime set.
	 *
	 * @param activating   the RealtimeBoolean activating the conditional
	 * @param deactivating the RealtimeBoolean deactivating the conditional
	 * @return a one-shot flip-flop
	 */
	public static RealtimeBoolean createFlipFlop(RealtimeBoolean activating, RealtimeBoolean deactivating) {
		return new FlipFlopRealtimeBoolean(activating, deactivating);
	}

	/**
	 * Creates a new RealtimeBoolean that is activated and deactivated by the
	 * supplied RealtimeBooleans.
	 *
	 * This RealtimeBoolean acts like a one-shot flip-flop, i.e., it is activated
	 * once when the activating RealtimeBoolean becomes true, and deactivated
	 * forever when the deactivating RealtimeBoolean becomes true.
	 *
	 * If both the activating and deactivating value are true at once, the created
	 * RealtimeBoolean is false (forever).
	 *
	 * @param activating   the RealtimeBoolean activating the conditional
	 * @param deactivating the RealtimeBoolean deactivating the conditional
	 * @param runtime      the RoboticsRuntime of the created RealtimeBoolean
	 * @return a one-shot flip-flop
	 */
	public static RealtimeBoolean createFlipFlop(RealtimeBoolean activating, RealtimeBoolean deactivating,
			RoboticsRuntime runtime) {
		return new FlipFlopRealtimeBoolean(activating, deactivating, runtime);
	}

	/**
	 * Creates a new RealtimeBoolean that is activated and deactivated by the
	 * supplied RealtimeBooleans.
	 *
	 * This RealtimeBoolean acts like a flip-flop, i.e., it is activated when the
	 * activating RealtimeBoolean becomes true, and deactivated when the
	 * deactivating RealtimeBoolean becomes true.
	 *
	 * If both the activating and deactivating value are true at once, the created
	 * RealtimeBoolean is false (forever).
	 *
	 * If oneShot is set, the RealtimeBoolean is deactivated forever once the
	 * deactivating value becomes true the first time.
	 *
	 * @param activating   the RealtimeBoolean activating the conditional
	 * @param deactivating the RealtimeBoolean deactivating the conditional
	 * @param oneShot      if the created RealtimeBoolean should act as a one-shot
	 *                     flip-flop
	 * @return a flip-flop
	 */
	public static RealtimeBoolean createFlipFlop(RealtimeBoolean activating, RealtimeBoolean deactivating,
			boolean oneShot) {
		return new FlipFlopRealtimeBoolean(activating, deactivating, oneShot);
	}

	/**
	 * Creates a new RealtimeBoolean that is activated and deactivated by the
	 * supplied RealtimeBooleans.
	 *
	 * This RealtimeBoolean acts like a flip-flop, i.e., it is activated when the
	 * activating RealtimeBoolean becomes true, and deactivated when the
	 * deactivating RealtimeBoolean becomes true.
	 *
	 * If both the activating and deactivating value are true at once, the created
	 * RealtimeBoolean is false (forever).
	 *
	 * If oneShot is set, the RealtimeBoolean is deactivated forever once the
	 * deactivating value becomes true the first time.
	 *
	 * @param activating   the RealtimeBoolean activating the conditional
	 * @param deactivating the RealtimeBoolean deactivating the conditional
	 * @param oneShot      if the created RealtimeBoolean should act as a one-shot
	 *                     flip-flop
	 * @param runtime      the RoboticsRuntime of the created RealtimeBoolean
	 * @return a flip-flop
	 */
	public static RealtimeBoolean createFlipFlop(RealtimeBoolean activating, RealtimeBoolean deactivating,
			boolean oneShot, RoboticsRuntime runtime) {
		return new FlipFlopRealtimeBoolean(activating, deactivating, oneShot, runtime);
	}

	/**
	 * Creates a new RealtimeBoolean whose value can be altered by the Robotics API
	 * application by invoking setValue(..).
	 *
	 * Altered values are propagated to any {@link RoboticsRuntime} that currently
	 * uses this RealtimeBoolean, e.g. as part of running {@link Command}s. However,
	 * no timing guarantees are given for the propagation.
	 *
	 * @param defaultValue the initial value of the RealtimeBoolean
	 * @return a writable RealtimeBoolean
	 */
	public static WritableRealtimeBoolean createWritable(boolean defaultValue) {
		return new WritableRealtimeBoolean(defaultValue);
	}

}
