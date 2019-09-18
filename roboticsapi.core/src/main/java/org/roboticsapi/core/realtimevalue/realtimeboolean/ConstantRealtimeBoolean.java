/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import java.util.Objects;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;

/**
 * This class represents a constant {@link Boolean} value available in real-time
 * in an execution environment.
 *
 * @see RealtimeValue
 * @see RealtimeBoolean
 * @see RoboticsRuntime
 */
public final class ConstantRealtimeBoolean extends RealtimeBoolean {

	/**
	 * The constant Boolean value
	 */
	private final Boolean value;

	/**
	 * Constructs a new constant {@link RealtimeBoolean}.
	 *
	 * @param value Boolean value which will be constant over time
	 */
	ConstantRealtimeBoolean(Boolean value) {
		this.value = Objects.requireNonNull(value, "Specified Boolean value must not be null!");
	}

	/**
	 * Return the constant Boolean value
	 *
	 * @return the constant value
	 */
	public Boolean getValue() {
		return value;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	protected Boolean calculateCheapValue() {
		return getValue();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value == ((ConstantRealtimeBoolean) obj).value;
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
	public RealtimeBoolean and(RealtimeBoolean other) {
		if (!CONSTANT_FOLDING) {
			return super.and(other);
		}
		if (!value) {
			return this;
		} else {
			return other;
		}
	}

	@Override
	public RealtimeBoolean or(RealtimeBoolean other) {
		if (!CONSTANT_FOLDING) {
			return super.or(other);
		}
		if (value) {
			return this;
		} else {
			return other;
		}
	}

	@Override
	public RealtimeBoolean not() {
		return CONSTANT_FOLDING ? createFromConstant(!value) : super.not();
	}

	@Override
	public RealtimeBoolean xor(RealtimeBoolean other) {
		if (!CONSTANT_FOLDING) {
			return super.xor(other);
		}
		if (value) {
			return other.not();
		} else {
			return other;
		}
	}
}
