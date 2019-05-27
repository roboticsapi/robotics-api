/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

/**
 * This class implements a {@link BinaryFunctionDoubleSensor} that divides its
 * two inner {@link DoubleSensor}s.
 */
public final class DividedDoubleSensor extends BinaryFunctionDoubleSensor {

	/**
	 * Constructor.
	 *
	 * @param dividend the dividend sensor.
	 * @param divisor  the divisor sensor.
	 */
	public DividedDoubleSensor(Sensor<Double> dividend, Sensor<Double> divisor) {
		super(dividend, divisor);
	}

	/**
	 * Returns the dividend sensor.
	 *
	 * @return the dividend sensor.
	 */
	public Sensor<Double> getDividend() {
		return getSensor1();
	}

	/**
	 * Returns the divisor sensor.
	 *
	 * @return the divisor sensor.
	 */
	public Sensor<Double> getDivisor() {
		return getSensor2();
	}

	@Override
	public String toString() {
		return "(" + getSensor1() + " / " + getSensor2() + ")";
	}

	@Override
	protected Double computeCheapValue(double dividend, double divisor) {
		return dividend / divisor;
	}

}
