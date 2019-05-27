/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

/**
 * This class implements a {@link BinaryFunctionDoubleSensor} that calculates
 * atan2 with its two inner {@link DoubleSensor}s.
 */
public final class Atan2DoubleSensor extends BinaryFunctionDoubleSensor {

	/**
	 * Constructor.
	 *
	 * @param y the sensor for Y.
	 * @param x the sensor for X.
	 */
	public Atan2DoubleSensor(Sensor<Double> y, Sensor<Double> x) {
		super(y, x);
	}

	/**
	 * Returns the sensor for Y.
	 *
	 * @return the sensor for Y.
	 */
	public Sensor<Double> getY() {
		return getSensor1();
	}

	/**
	 * Returns the sensor for X.
	 *
	 * @return the sensor for X.
	 */
	public Sensor<Double> getX() {
		return getSensor2();
	}

	@Override
	public String toString() {
		return "atan2(" + getSensor1() + ", " + getSensor2() + ")";
	}

	@Override
	protected Double computeCheapValue(double y, double x) {
		return Math.atan2(y, x);
	}
}
