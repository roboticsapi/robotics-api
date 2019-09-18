/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

/**
 * This class implements a {@link BinaryFunctionRealtimeDouble} that calculates
 * atan2 with its two inner {@link RealtimeDouble}s.
 */
public final class Atan2RealtimeDouble extends BinaryFunctionRealtimeDouble {

	/**
	 * Constructor.
	 *
	 * @param y the {@link RealtimeDouble} for Y.
	 * @param x the {@link RealtimeDouble} for X.
	 */
	Atan2RealtimeDouble(RealtimeDouble y, RealtimeDouble x) {
		super(y, x);
	}

	/**
	 * Returns the {@link RealtimeDouble} for Y.
	 *
	 * @return the operand Y.
	 */
	public RealtimeDouble getY() {
		return getFirstValue();
	}

	/**
	 * Returns the {@link RealtimeDouble} for X.
	 *
	 * @return the operand X.
	 */
	public RealtimeDouble getX() {
		return getSecondValue();
	}

	@Override
	public String toString() {
		return "atan2(" + getFirstValue() + ", " + getSecondValue() + ")";
	}

	@Override
	protected Double computeCheapValue(double y, double x) {
		return Math.atan2(y, x);
	}
}
