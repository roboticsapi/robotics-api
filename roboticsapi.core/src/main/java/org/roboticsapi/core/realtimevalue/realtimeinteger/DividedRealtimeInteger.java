/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

/**
 * This class implements a {@link DerivedRealtimeInteger} that divides its two
 * inner {@link RealtimeInteger}s.
 */
public final class DividedRealtimeInteger extends DerivedRealtimeInteger {

	/**
	 * Constructor.
	 *
	 * @param dividend the dividend sensor.
	 * @param divisor  the divisor sensor.
	 */
	DividedRealtimeInteger(RealtimeInteger dividend, RealtimeInteger divisor) {
		super(dividend, divisor);
	}

	/**
	 * Returns the dividend sensor.
	 *
	 * @return the dividend sensor.
	 */
	public RealtimeInteger getDividend() {
		return (RealtimeInteger) getInner(0);
	}

	/**
	 * Returns the divisor sensor.
	 *
	 * @return the divisor sensor.
	 */
	public RealtimeInteger getDivisor() {
		return (RealtimeInteger) getInner(1);
	}

	@Override
	public String toString() {
		return "(" + getDividend() + " / " + getDivisor() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return (Integer) values[0] / (Integer) values[1];
	}

}
