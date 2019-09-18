/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import org.roboticsapi.core.realtimevalue.WritableRealtimeValue;

public final class WritableRealtimeDoubleArray extends RealtimeDoubleArray implements WritableRealtimeValue<Double[]> {

	private Double[] currentValue;

	WritableRealtimeDoubleArray(Double[] defaultValue) {
		super(null, defaultValue.length);
		this.currentValue = defaultValue;
	}

	@Override
	public void setValue(Double[] value) {
		if (value != currentValue) {
			currentValue = value;
			notifyCheapValueChanged();
		}
	}

	@Override
	protected Double[] calculateCheapValue() {
		return currentValue;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "fromJava(" + currentValue + ")";
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public Double[] getValue() {
		return currentValue;
	}

}
