/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Rotation;

public class SlidingAverageRotationSensor extends RotationSensor {

	private final RotationSensor otherSensor;
	private final double duration;

	public SlidingAverageRotationSensor(RotationSensor otherSensor, double duration) {
		super(selectRuntime(otherSensor));
		addInnerSensors(otherSensor);
		this.otherSensor = otherSensor;
		this.duration = duration;
	}

	public RotationSensor getOtherSensor() {
		return otherSensor;
	}

	public double getDuration() {
		return duration;
	}

	@Override
	protected Rotation calculateCheapValue() {
		if (otherSensor.getRuntime() == null) {
			throw new IllegalArgumentException("Cannot calculate sliding average for Sensor without runtime");
		} else {
			return super.getCheapValue();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && duration == ((SlidingAverageRotationSensor) obj).duration
				&& otherSensor.equals(((SlidingAverageRotationSensor) obj).otherSensor);
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
