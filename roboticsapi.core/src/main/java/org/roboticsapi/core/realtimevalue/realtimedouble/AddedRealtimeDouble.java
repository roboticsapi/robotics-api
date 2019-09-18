/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;

/**
 * This class implements a {@link BinaryFunctionRealtimeDouble} that adds its
 * two inner {@link RealtimeDouble}s.
 */
public final class AddedRealtimeDouble extends BinaryFunctionRealtimeDouble {

	@Override
	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new AddedRealtimeDouble(getFirstValue().substitute(substitutionMap),
				getSecondValue().substitute(substitutionMap));
	}

	/**
	 * Constructor.
	 *
	 * @param value1 the first operand.
	 * @param value2 the second operand.
	 */
	AddedRealtimeDouble(RealtimeDouble value1, RealtimeDouble value2) {
		super(value1, value2);
	}

	@Override
	public String toString() {
		return "(" + getFirstValue() + " + " + getSecondValue() + ")";
	}

	@Override
	protected Double computeCheapValue(double value1, double value2) {
		return value1 + value2;
	}

	@Override
	public RealtimeDouble add(RealtimeDouble other) {
		if (CONSTANT_FOLDING && other.isConstant() && getFirstValue().isConstant()) {
			return getFirstValue().add(other).add(getSecondValue());
		}
		if (CONSTANT_FOLDING && other.isConstant() && getSecondValue().isConstant()) {
			return getFirstValue().add(getSecondValue().add(other));
		}
		return super.add(other);
	}
}
