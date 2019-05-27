/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

public final class InvertedVelocitySensor extends VelocitySensor {

	private final VelocitySensor otherSensor;

	public InvertedVelocitySensor(VelocitySensor otherSensor) {
		super(selectRuntime(otherSensor), otherSensor.getReferenceFrame(), otherSensor.getMovingFrame(),
				otherSensor.getPivotPoint(), otherSensor.getOrientation());
		addInnerSensors(otherSensor);
		this.otherSensor = otherSensor;
	}

	public VelocitySensor getOtherSensor() {
		return otherSensor;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && otherSensor.equals(((InvertedVelocitySensor) obj).otherSensor);
	}

	@Override
	public int hashCode() {
		return classHash(otherSensor);
	}

	@Override
	public boolean isAvailable() {
		return otherSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "((" + otherSensor + ") ^ -1)";
	}
}
