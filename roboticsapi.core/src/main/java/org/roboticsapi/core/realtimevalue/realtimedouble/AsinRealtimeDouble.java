/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

/**
 * This class implements a {@link UnaryFunctionRealtimeDouble} that computes the
 * arc sine of the inner sensor.
 */
public final class AsinRealtimeDouble extends UnaryFunctionRealtimeDouble<Double> {

	/**
	 * Constructor.
	 *
	 * @param other the inner sensor.
	 */
	AsinRealtimeDouble(RealtimeDouble other) {
		super(other);
	}

	@Override
	public String toString() {
		return "asin(" + getInnerValue() + ")";
	}

	@Override
	protected Double computeCheapValue(Double value) {
		return Math.asin(value);
	}
}
