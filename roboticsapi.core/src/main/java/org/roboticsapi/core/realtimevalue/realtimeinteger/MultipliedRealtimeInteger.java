/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

/**
 * This class implements a {@link DerivedRealtimeInteger} that multiplies its
 * two inner {@link RealtimeInteger}s.
 */
public final class MultipliedRealtimeInteger extends DerivedRealtimeInteger {

	/**
	 * Constructor.
	 *
	 * @param multiplicand the multiplicand sensor.
	 * @param multiplier   the multiplier sensor.
	 */
	MultipliedRealtimeInteger(RealtimeInteger multiplicand, RealtimeInteger multiplier) {
		super(multiplicand, multiplier);
	}

	/**
	 * Returns the multiplicand sensor.
	 *
	 * @return the multiplicand sensor.
	 */
	public final RealtimeInteger getMultiplicand() {
		return (RealtimeInteger) getInner(0);
	}

	/**
	 * Returns the multiplier sensor.
	 *
	 * @return the multiplier sensor.
	 */
	public final RealtimeInteger getMultiplier() {
		return (RealtimeInteger) getInner(1);
	}

	@Override
	public String toString() {
		return "(" + getMultiplicand() + " + " + getMultiplier() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return ((Integer) values[0]) * ((Integer) values[1]);
	}
}
