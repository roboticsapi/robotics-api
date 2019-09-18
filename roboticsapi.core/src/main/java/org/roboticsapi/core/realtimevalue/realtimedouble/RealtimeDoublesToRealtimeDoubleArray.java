/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

public final class RealtimeDoublesToRealtimeDoubleArray extends RealtimeDoubleArray {

	private final RealtimeDouble[] doubles;

	RealtimeDoublesToRealtimeDoubleArray(RealtimeDouble[] doubles) {
		super(doubles.length, doubles);
		this.doubles = doubles;
	}

	@Override
	public RealtimeDouble[] getDoubles() {
		return doubles;
	}

	@Override
	public RealtimeDouble get(int index) {
		return doubles[index];
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && doubles.equals(((RealtimeDoublesToRealtimeDoubleArray) obj).doubles);
	}

	@Override
	public int hashCode() {
		return classHash((Object) doubles);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(doubles);
	}

	@Override
	public String toString() {
		String ret = "";
		for (RealtimeDouble sensor : doubles) {
			ret += sensor + ", ";
		}
		if (doubles.length != 0) {
			ret = ret.substring(0, ret.length() - 3);
		}
		return "array(" + ret + ")";
	}
}
