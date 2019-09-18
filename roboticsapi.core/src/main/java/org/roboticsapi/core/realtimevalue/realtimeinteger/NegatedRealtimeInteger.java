/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

/**
 * This class implements a {@link DerivedRealtimeInteger} that negates the value
 * of the inner {@link RealtimeInteger}.
 */
public final class NegatedRealtimeInteger extends DerivedRealtimeInteger {

	/**
	 * Constructor.
	 *
	 * @param other the inner sensor.
	 */
	NegatedRealtimeInteger(RealtimeInteger other) {
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
		return "(- " + getInnerValue() + ")";
	}

	@Override
	public RealtimeInteger negate() {
		return getInnerValue();
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return -(Integer) values[0];
	}

}
