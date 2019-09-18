/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

/**
 * This class implements a {@link DerivedRealtimeInteger} that computes the
 * absolute value of the inner {@link RealtimeInteger}.
 */
public final class AbsRealtimeInteger extends DerivedRealtimeInteger {

	/**
	 * Constructor.
	 *
	 * @param other the inner sensor.
	 */
	AbsRealtimeInteger(RealtimeInteger other) {
		super(other);
	}

	/**
	 * Returns the inner sensor.
	 *
	 * @return the inner sensor.
	 */
	public final RealtimeInteger getInnerValue() {
		return (RealtimeInteger) getInner(0);
	}

	@Override
	public String toString() {
		return "abs(" + getInnerValue() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return Math.abs((Integer) values[0]);
	}
}
