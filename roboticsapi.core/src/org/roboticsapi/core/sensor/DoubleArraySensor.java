/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;

/**
 * Gives access to double values of Sensors.
 */
public abstract class DoubleArraySensor extends Sensor<Double[]> {
	private final int size;

	/**
	 * @param runtime
	 */
	public DoubleArraySensor(final RoboticsRuntime runtime, int size) {
		super(runtime);
		this.size = size;
	}

	@Override
	protected Double[] getDefaultValue() {
		return new Double[size];
	}

	public int getSize() {
		return size;
	}

	public DoubleSensor[] getSensors() {
		DoubleSensor[] ret = new DoubleSensor[size];
		for (int i = 0; i < size; i++) {
			ret[i] = getSensor(i);
		}
		return ret;
	}

	public DoubleSensor getSensor(int index) {
		return new DoubleFromDoubleArraySensor(this, index);
	}

	public static DoubleArraySensor fromConstant(double... values) {
		DoubleSensor[] sensors = new DoubleSensor[values.length];

		for (int i = 0; i < sensors.length; i++) {
			sensors[i] = DoubleSensor.fromValue(values[i]);
		}
		return new DoubleArrayFromDoubleSensor(sensors);
	}

	public static DoubleArraySensor fromSensors(DoubleSensor... sensors) {
		return new DoubleArrayFromDoubleSensor(sensors);
	}

}
