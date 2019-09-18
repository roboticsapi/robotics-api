/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

/**
 * This class implements a {@link DerivedRealtimeInteger} that adds its two
 * inner {@link RealtimeInteger}s.
 */
public final class AddedRealtimeInteger extends DerivedRealtimeInteger {
	/**
	 * Constructor.
	 *
	 * @param addend1 the first addend.
	 * @param addend2 the second addend.
	 */
	AddedRealtimeInteger(RealtimeInteger addend1, RealtimeInteger addend2) {
		super(addend1, addend2);
	}

	public final RealtimeInteger getAddend1() {
		return (RealtimeInteger) getInner(0);
	}

	public final RealtimeInteger getAddend2() {
		return (RealtimeInteger) getInner(1);
	}

	@Override
	public String toString() {
		return "(" + getAddend1() + " + " + getAddend2() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return ((Integer) values[0]) + ((Integer) values[1]);
	}
}
