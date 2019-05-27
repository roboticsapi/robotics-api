/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link BinaryFunctionDoubleSensor} that calculates
 * base raised to power exponent {@link DoubleSensor}s.
 */
public final class PowerDoubleSensor extends BinaryFunctionDoubleSensor {

	/**
	 * Constructor.
	 *
	 * @param base     the base sensor.
	 * @param exponent the exponent sensor.
	 */
	public PowerDoubleSensor(DoubleSensor base, DoubleSensor exponent) {
		super(base, exponent);
	}

	@Override
	public String toString() {
		return "(" + getSensor1() + " ^ " + getSensor2() + ")";
	}

	@Override
	protected Double computeCheapValue(double value1, double value2) {
		return Math.pow(value1, value2);
	}
}
