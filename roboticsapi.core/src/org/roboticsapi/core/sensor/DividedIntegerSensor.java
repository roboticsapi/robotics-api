/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

/**
 * This class implements a {@link DerivedIntegerSensor} that divides its two
 * inner {@link IntegerSensor}s.
 */
public final class DividedIntegerSensor extends DerivedIntegerSensor {

	/**
	 * Constructor.
	 * 
	 * @param dividend the dividend sensor.
	 * @param divisor  the divisor sensor.
	 */
	public DividedIntegerSensor(Sensor<Integer> dividend, Sensor<Integer> divisor) {
		super(dividend, divisor);
	}

	/**
	 * Returns the dividend sensor.
	 * 
	 * @return the dividend sensor.
	 */
	@SuppressWarnings("unchecked")
	public Sensor<Integer> getDividend() {
		return (Sensor<Integer>) getInnerSensor(0);
	}

	/**
	 * Returns the divisor sensor.
	 * 
	 * @return the divisor sensor.
	 */
	@SuppressWarnings("unchecked")
	public Sensor<Integer> getDivisor() {
		return (Sensor<Integer>) getInnerSensor(1);
	}

	@Override
	public String toString() {
		return "(" + getDividend() + " / " + getDivisor() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return (Integer) values[0] / (Integer) values[1];
	}

}
