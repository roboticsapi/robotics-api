/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class ConstantDoubleSensor extends DoubleSensor {

	private final double value;

	public ConstantDoubleSensor(double value) {
		super(null);
		this.value = value;
	}

	@Override
	protected Double calculateCheapValue() {
		return getValue();
	}

	public double getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value == ((ConstantDoubleSensor) obj).value;
	}

	@Override
	public int hashCode() {
		return classHash(value);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "" + value;
	}
}
