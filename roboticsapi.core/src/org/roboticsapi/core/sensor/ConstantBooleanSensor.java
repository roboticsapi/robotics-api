/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class ConstantBooleanSensor extends BooleanSensor {

	private final Boolean value;

	public ConstantBooleanSensor(Boolean value) {
		super(null);
		this.value = value;
	}

	@Override
	protected Boolean calculateCheapValue() {
		return getValue();
	}

	public Boolean getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value == ((ConstantBooleanSensor) obj).value;
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
