/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link DerivedIntegerSensor} that adds its two inner
 * {@link IntegerSensor}s.
 */
public final class AddedIntegerSensor extends DerivedIntegerSensor {

	/**
	 * Constructor.
	 * 
	 * @param addend1 the first addend sensor.
	 * @param addend2 the second addend sensor.
	 */
	public AddedIntegerSensor(IntegerSensor addend1, IntegerSensor addend2) {
		super(addend1, addend2);
	}

	public final IntegerSensor getAddend1() {
		return (IntegerSensor) getInnerSensor(0);
	}

	public final IntegerSensor getAddend2() {
		return (IntegerSensor) getInnerSensor(1);
	}

	@Override
	public String toString() {
		return "(" + getAddend1() + " + " + getAddend2() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return ((Integer) values[0]) + ((Integer) values[1]);
	}
}
