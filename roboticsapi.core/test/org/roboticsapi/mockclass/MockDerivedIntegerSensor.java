/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.sensor.DerivedIntegerSensor;
import org.roboticsapi.core.sensor.IntegerSensor;

public class MockDerivedIntegerSensor extends DerivedIntegerSensor {
	public MockDerivedIntegerSensor(IntegerSensor sensor, IntegerSensor... sensors) {
		super(getSensorArray(sensor, sensors));
	}

	private static IntegerSensor[] getSensorArray(IntegerSensor sensor, IntegerSensor... sensors) {
		IntegerSensor[] tempSensors = new IntegerSensor[sensors.length + 1];
		tempSensors[0] = sensor;
		System.arraycopy(sensors, 0, tempSensors, 1, sensors.length);
		return tempSensors;
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return (Integer) values[0];
	}
}
