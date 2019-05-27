/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link UnaryFunctionDoubleSensor} that negates the
 * value of the inner sensor.
 */
public final class NegatedDoubleSensor extends UnaryFunctionDoubleSensor<Double> {

	/**
	 * Constructor.
	 * 
	 * @param other the inner sensor.
	 */
	public NegatedDoubleSensor(DoubleSensor other) {
		super(other);
	}

	@Override
	public String toString() {
		return "(- " + getInnerSensor() + ")";
	}

	@Override
	protected Double computeCheapValue(Double value) {
		return -value;
	}
}
