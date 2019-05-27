/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

/**
 * This class implements a {@link BinaryFunctionDoubleSensor} that multiplies
 * its two inner {@link DoubleSensor}s.
 */
public final class MultipliedDoubleSensor extends BinaryFunctionDoubleSensor {

	/**
	 * Constructor.
	 * 
	 * @param sensor1 the first sensor.
	 * @param sensor2 the second sensor.
	 */
	public MultipliedDoubleSensor(DoubleSensor sensor1, DoubleSensor sensor2) {
		super(sensor1, sensor2);
	}

	@Override
	public String toString() {
		return "(" + getSensor1() + " * " + getSensor2() + ")";
	}

	@Override
	protected Double computeCheapValue(double value1, double value2) {
		return value1 * value2;
	}
}
