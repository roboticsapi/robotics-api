/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

public class IntegratedDoubleSensor extends BinaryFunctionDoubleSensor {

	public IntegratedDoubleSensor(Sensor<Double> initial, Sensor<Double> increment) {
		super(initial, increment);
	}

	@Override
	protected Double computeCheapValue(double value1, double value2) {
		return null;
	}

	public Sensor<Double> getInitial() {
		return getSensor1();
	}

	public Sensor<Double> getIncrement() {
		return getSensor2();
	}
}
