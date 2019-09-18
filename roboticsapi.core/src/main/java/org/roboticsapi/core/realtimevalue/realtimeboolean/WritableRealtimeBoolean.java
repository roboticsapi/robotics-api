/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.realtimevalue.WritableRealtimeValue;

public final class WritableRealtimeBoolean extends RealtimeBoolean implements WritableRealtimeValue<Boolean> {

	private boolean lastValue;

	WritableRealtimeBoolean(boolean defaultValue) {
		this.lastValue = defaultValue;
	}

	@Override
	public void setValue(Boolean value) {
		if (value != lastValue) {
			lastValue = value;
			notifyCheapValueChanged();
		}
	}

	@Override
	protected Boolean calculateCheapValue() {
		return lastValue;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "fromJava(" + lastValue + ")";
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public Boolean getValue() {
		return lastValue;
	}

}
