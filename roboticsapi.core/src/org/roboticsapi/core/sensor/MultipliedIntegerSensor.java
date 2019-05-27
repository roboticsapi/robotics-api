/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link DerivedIntegerSensor} that multiplies its two
 * inner {@link IntegerSensor}s.
 */
public final class MultipliedIntegerSensor extends DerivedIntegerSensor {

	/**
	 * Constructor.
	 * 
	 * @param multiplicand the multiplicand sensor.
	 * @param multiplier   the multiplier sensor.
	 */
	public MultipliedIntegerSensor(IntegerSensor multiplicand, IntegerSensor multiplier) {
		super(multiplicand, multiplier);
	}

	/**
	 * Returns the multiplicand sensor.
	 * 
	 * @return the multiplicand sensor.
	 */
	public final IntegerSensor getMultiplicand() {
		return (IntegerSensor) getInnerSensor(0);
	}

	/**
	 * Returns the multiplier sensor.
	 * 
	 * @return the multiplier sensor.
	 */
	public final IntegerSensor getMultiplier() {
		return (IntegerSensor) getInnerSensor(1);
	}

	@Override
	public String toString() {
		return "(" + getMultiplicand() + " * " + getMultiplier() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return ((Integer) values[0]) * ((Integer) values[1]);
	}
}
