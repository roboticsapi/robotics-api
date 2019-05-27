/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class IntegerFromJavaSensor extends IntegerSensor {

	private int currentValue;

	public IntegerFromJavaSensor(int defaultValue) {
		super(null);
		this.currentValue = defaultValue;
	}

	@Override
	public Integer getDefaultValue() {
		return currentValue;
	}

	public void setValue(int value) {
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
}
