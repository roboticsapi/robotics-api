/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

public final class GetFromArrayRealtimeDouble extends UnaryFunctionRealtimeDouble<Double[]> {

	private final int index;

	GetFromArrayRealtimeDouble(RealtimeDoubleArray doubleArray, int index) {
		super(doubleArray);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public RealtimeDoubleArray getDoubleArray() {
		return (RealtimeDoubleArray) getInnerValue();
	}

	@Override
	protected boolean equals2(UnaryFunctionRealtimeDouble<?> obj) {
		return index == ((GetFromArrayRealtimeDouble) obj).index;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { index };
	}

	@Override
	public String toString() {
		return getDoubleArray() + "[" + index + "]";
	}

	@Override
	protected Double computeCheapValue(Double[] value) {
		return value[index];
	}
}
