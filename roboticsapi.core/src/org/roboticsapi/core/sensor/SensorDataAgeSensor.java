/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

public final class SensorDataAgeSensor extends DoubleSensor {

	private final Sensor<?> sensor;

	public SensorDataAgeSensor(Sensor<?> sensor) {
		super(selectRuntime(sensor));
		addInnerSensors(sensor);
		this.sensor = sensor;
	}

	public Sensor<?> getSensor() {
		return sensor;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensor.equals(((SensorDataAgeSensor) obj).sensor);
	}

	@Override
	public int hashCode() {
		return classHash(sensor);
	}

	@Override
	public boolean isAvailable() {
		return sensor.isAvailable();
	}

	@Override
	public String toString() {
		return "age(" + sensor + ")";
	}
}
