/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class DoubleFromJavaSensor extends DoubleSensor {

	private double currentValue;

	public DoubleFromJavaSensor(double defaultValue) {
		super(null);
		this.currentValue = defaultValue;
	}

	@Override
	public Double getDefaultValue() {
		return currentValue;
	}

	public void setValue(double value) {
		if (value != currentValue) {
			currentValue = value;
			notifyCheapValueChanged();
		}
	}

	@Override
	protected Double calculateCheapValue() {
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
