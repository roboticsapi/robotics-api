/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link UnaryFunctionDoubleSensor} that calculates the
 * sine of its inner {@link DoubleSensor}.
 */
public final class SineDoubleSensor extends UnaryFunctionDoubleSensor<Double> {

	/**
	 * Constructor.
	 *
	 * @param d the sensor for calculating the sine (in rad).
	 */
	public SineDoubleSensor(DoubleSensor d) {
		super(d);
	}

	@Override
	public String toString() {
		return "sin(" + getInnerSensor() + ")";
	}

	@Override
	protected Double computeCheapValue(Double value) {
		return Math.sin(value);
	}

}
