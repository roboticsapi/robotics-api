/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

/**
 * This class implements a {@link UnaryFunctionRealtimeDouble} that negates the
 * value of the inner sensor.
 */
public final class NegatedRealtimeDouble extends UnaryFunctionRealtimeDouble<Double> {

	/**
	 * Constructor.
	 *
	 * @param other the inner sensor.
	 */
	NegatedRealtimeDouble(RealtimeDouble other) {
		super(other);
	}

	@Override
	public String toString() {
		return "(- " + getInnerValue() + ")";
	}

	@Override
	public RealtimeDouble negate() {
		return (RealtimeDouble) getInnerValue();
	}

	@Override
	protected Double computeCheapValue(Double value) {
		return -value;
	}
}
