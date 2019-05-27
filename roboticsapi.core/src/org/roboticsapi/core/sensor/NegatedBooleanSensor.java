/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class NegatedBooleanSensor extends BooleanSensor {

	private final BooleanSensor sensor;

	public NegatedBooleanSensor(BooleanSensor sensor) {
		super(selectRuntime(sensor));
		addInnerSensors(sensor);
		this.sensor = sensor;
	}

	public BooleanSensor getOtherSensor() {
		return sensor;
	}

	@Override
	public BooleanSensor not() {
		return sensor;
	}

	@Override
	protected Boolean calculateCheapValue() {
		Boolean cheapValue = sensor.getCheapValue();
		if (cheapValue == null) {
			return null;
		}
		return !cheapValue;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensor.equals(((NegatedBooleanSensor) obj).sensor);
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
		return "!(" + sensor + ")";
	}
}
