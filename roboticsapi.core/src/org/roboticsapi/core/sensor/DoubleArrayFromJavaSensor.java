/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import java.util.Arrays;

public final class DoubleArrayFromJavaSensor extends DoubleArraySensor {

	private Double[] currentValue;

	public DoubleArrayFromJavaSensor(Double[] defaultValue) {
		super(null, defaultValue.length);
		this.currentValue = defaultValue;
	}

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
		return "fromJava(" + Arrays.toString(currentValue) + ")";
	}

}
