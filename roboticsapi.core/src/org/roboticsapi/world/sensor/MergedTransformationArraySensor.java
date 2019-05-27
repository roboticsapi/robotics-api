/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Transformation;

public class MergedTransformationArraySensor extends TransformationArraySensor {

	private final TransformationSensor[] sensors;

	public MergedTransformationArraySensor(TransformationSensor[] sensors) {
		super(selectRuntime(sensors), sensors.length);
		addInnerSensors(sensors);
		this.sensors = sensors;
	}

	@Override
	public TransformationSensor[] getSensors() {
		return sensors;
	}

	@Override
	public TransformationSensor getSensor(int index) {
		return sensors[index];
	}

	@Override
	protected Transformation[] calculateCheapValue() {
		Transformation[] ret = new Transformation[sensors.length];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = sensors[i].getCheapValue();

			if (ret[i] == null) {
				return null;
			}
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensors.equals(((MergedTransformationArraySensor) obj).sensors);
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
		for (TransformationSensor sensor : sensors) {
			ret += sensor + ", ";
		}
		if (sensors.length != 0) {
			ret = ret.substring(0, ret.length() - 3);
		}
		return "array(" + ret + ")";
	}

}
