/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

public final class SlidingAverageDoubleSensor extends DoubleSensor {

	private final Sensor<Double> otherSensor;
	private final double duration;

	public SlidingAverageDoubleSensor(Sensor<Double> otherSensor, double duration) {
		super(selectRuntime(otherSensor));
		addInnerSensors(otherSensor);
		this.otherSensor = otherSensor;
		this.duration = duration;
	}

	public Sensor<Double> getOtherSensor() {
		return otherSensor;
	}

	public double getDuration() {
		return duration;
	}

	// cheap value calculation does not make sense for this kind of sensor
	// @Override
	// protected Double calculateCheapValue() {
	// if (otherSensor.getRuntime() == null) {
	// throw new IllegalArgumentException(
	// "Cannot calculate sliding average for Sensor without runtime");
	// } else {
	// return super.getCheapValue();
	// }
	//
	// }

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && duration == ((SlidingAverageDoubleSensor) obj).duration
				&& otherSensor.equals(((SlidingAverageDoubleSensor) obj).otherSensor);
	}

	@Override
	public int hashCode() {
		return classHash(otherSensor, duration);
	}

	@Override
	public boolean isAvailable() {
		return otherSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "slidingAverage(" + otherSensor + ", " + duration + ")";
	}
}
