/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.realtimevalue.WritableRealtimeValue;
import org.roboticsapi.core.world.Transformation;

public final class WritableRealtimeTransformation extends RealtimeTransformation
		implements WritableRealtimeValue<Transformation> {

	private Transformation currentValue;

	WritableRealtimeTransformation(Transformation defaultValue) {
		this.currentValue = defaultValue;
	}

	@Override
	public void setValue(Transformation value) {
		if (value != currentValue) {
			currentValue = value;
			notifyCheapValueChanged();
		}
	}

	@Override
	protected Transformation calculateCheapValue() {
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
	public Transformation getValue() {
		return currentValue;
	}

}
