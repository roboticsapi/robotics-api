/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class BooleanFromJavaSensor extends BooleanSensor {

	private boolean lastValue;

	public BooleanFromJavaSensor(boolean defaultValue) {
		super(null);
		this.lastValue = defaultValue;
	}

	@Override
	public Boolean getDefaultValue() {
		return lastValue;
	}

	public void setValue(boolean value) {
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
}
