/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class DoubleArrayFromDoubleSensor extends DoubleArraySensor {

	private final DoubleSensor[] sensors;

	public DoubleArrayFromDoubleSensor(DoubleSensor[] sensors) {
		super(selectRuntime(sensors), sensors.length);
		addInnerSensors(sensors);
		this.sensors = sensors;
	}

	@Override
	protected Double[] calculateCheapValue() {
		Double[] ret = new Double[sensors.length];

		for (int i = 0; i < sensors.length; i++) {
			Double value = sensors[i].getCheapValue();

			if (value == null) {
				return null;
			}
			ret[i] = value;
		}
		return ret;
	}

	@Override
	public DoubleSensor[] getSensors() {
		return sensors;
	}

	@Override
	public DoubleSensor getSensor(int index) {
		return sensors[index];
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensors.equals(((DoubleArrayFromDoubleSensor) obj).sensors);
	}

	@Override
	public int hashCode() {
		return classHash((Object) sensors);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(sensors);
	}

	@Override
	public String toString() {
		String ret = "";
		for (DoubleSensor sensor : sensors) {
			ret += sensor + ", ";
		}
		if (sensors.length != 0) {
			ret = ret.substring(0, ret.length() - 3);
		}
		return "array(" + ret + ")";
	}
}
