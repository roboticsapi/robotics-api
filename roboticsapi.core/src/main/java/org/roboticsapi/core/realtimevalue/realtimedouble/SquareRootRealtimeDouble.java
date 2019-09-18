/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

/**
 * This class implements a {@link UnaryFunctionRealtimeDouble} that calculates
 * the square root of the inner sensor.
 */
public final class SquareRootRealtimeDouble extends UnaryFunctionRealtimeDouble<Double> {

	/**
	 * Constructor.
	 *
	 * @param other the other sensor.
	 */
	SquareRootRealtimeDouble(RealtimeDouble other) {
		super(other);
	}

	@Override
	public String toString() {
		return "sqrt(" + getInnerValue() + ")";
	}

	@Override
	protected Double computeCheapValue(Double value) {
		return Math.sqrt(value);
	}
}
