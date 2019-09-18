/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

/**
 * This class implements a {@link BinaryFunctionRealtimeDouble} that divides its
 * two inner {@link RealtimeDouble}s.
 */
public final class DividedRealtimeDouble extends BinaryFunctionRealtimeDouble {

	/**
	 * Constructor.
	 *
	 * @param dividend the dividend.
	 * @param divisor  the divisor.
	 */
	DividedRealtimeDouble(RealtimeDouble dividend, RealtimeDouble divisor) {
		super(dividend, divisor);
	}

	/**
	 * Returns the dividend.
	 *
	 * @return the dividend.
	 */
	public RealtimeDouble getDividend() {
		return getFirstValue();
	}

	/**
	 * Returns the divisor.
	 *
	 * @return the divisor.
	 */
	public RealtimeDouble getDivisor() {
		return getSecondValue();
	}

	@Override
	public String toString() {
		return "(" + getFirstValue() + " / " + getSecondValue() + ")";
	}

	@Override
	protected Double computeCheapValue(double dividend, double divisor) {
		return dividend / divisor;
	}

	@Override
	public RealtimeDouble divide(RealtimeDouble other) {
		if (CONSTANT_FOLDING && other.isConstant() && getDividend().isConstant()) {
			return getDividend().divide(other).divide(getDivisor());
		}
		if (CONSTANT_FOLDING && other.isConstant() && getDivisor().isConstant()) {
			return getDividend().divide(getDivisor().multiply(other));
		}
		return super.divide(other);
	}

}
