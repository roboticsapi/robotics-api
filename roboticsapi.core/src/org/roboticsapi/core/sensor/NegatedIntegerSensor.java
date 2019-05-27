/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link DerivedIntegerSensor} that negates the value
 * of the inner {@link IntegerSensor}.
 */
public final class NegatedIntegerSensor extends DerivedIntegerSensor {

	/**
	 * Constructor.
	 * 
	 * @param other the inner sensor.
	 */
	public NegatedIntegerSensor(IntegerSensor other) {
		super(other);
	}

	/**
	 * Returns the inner sensor.
	 * 
	 * @return the inner sensor.
	 */
	public final IntegerSensor getInnerSensor() {
		return (IntegerSensor) getInnerSensor(0);
	}

	@Override
	public String toString() {
		return "(- " + getInnerSensor() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return -(Integer) values[0];
	}

}
