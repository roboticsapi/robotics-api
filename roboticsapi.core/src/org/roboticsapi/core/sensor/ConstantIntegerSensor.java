/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * {@link IntegerSensor} representing a constant integer value.
 */
public final class ConstantIntegerSensor extends IntegerSensor {

	private final int value;

	/**
	 * Constructs a new {@link ConstantIntegerSensor}.
	 *
	 * @param value the constant value.
	 */
	public ConstantIntegerSensor(int value) {
		super(null);
		this.value = value;
	}

	@Override
	protected Integer calculateCheapValue() {
		return getValue();
	}

	public Integer getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value == ((ConstantIntegerSensor) obj).value;
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
