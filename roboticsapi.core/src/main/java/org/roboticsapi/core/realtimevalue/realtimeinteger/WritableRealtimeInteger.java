/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import org.roboticsapi.core.realtimevalue.WritableRealtimeValue;

public final class WritableRealtimeInteger extends RealtimeInteger implements WritableRealtimeValue<Integer> {

	private int currentValue;

	WritableRealtimeInteger(int defaultValue) {
		this.currentValue = defaultValue;
	}

	@Override
	public void setValue(Integer value) {
		if (value != currentValue) {
			currentValue = value;
			notifyCheapValueChanged();
		}
	}

	@Override
	protected Integer calculateCheapValue() {
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
		return this == obj;
	}

	@Override
	public Integer getValue() {
		return currentValue;
	}
}
