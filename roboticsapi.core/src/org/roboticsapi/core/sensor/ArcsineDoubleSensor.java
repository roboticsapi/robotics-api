/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link UnaryFunctionDoubleSensor} that calculates the
 * arcsine of its inner {@link DoubleSensor}.
 */
public final class ArcsineDoubleSensor extends UnaryFunctionDoubleSensor<Double> {

	/**
	 * Constructor.
	 *
	 * @param d the sensor for calculating the arcsine (in rad).
	 */
	public ArcsineDoubleSensor(DoubleSensor d) {
		super(d);
	}

	@Override
	public String toString() {
		return "arcsin(" + getInnerSensor() + ")";
	}

	@Override
	protected Double computeCheapValue(Double value) {
		return Math.asin(value);
	}

}
