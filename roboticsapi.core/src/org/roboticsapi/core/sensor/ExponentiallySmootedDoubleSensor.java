/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class ExponentiallySmootedDoubleSensor extends DoubleSensor {

	private final double halfLife;
	private final DoubleSensor otherSensor;

	public ExponentiallySmootedDoubleSensor(DoubleSensor otherSensor, double halfLife) {
		super(selectRuntime(otherSensor));
		addInnerSensors(otherSensor);
		this.otherSensor = otherSensor;
		this.halfLife = halfLife;
	}

	public double getHalfLife() {
		return halfLife;
	}

	public DoubleSensor getOtherSensor() {
		return otherSensor;
	}

	@Override
	protected Double calculateCheapValue() {
		if (otherSensor.getRuntime() == null) {
			throw new IllegalArgumentException("Cannot calculate sliding average for Sensor without runtime");
		} else {
			return super.getCheapValue();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && halfLife == ((ExponentiallySmootedDoubleSensor) obj).halfLife
				&& otherSensor.equals(((ExponentiallySmootedDoubleSensor) obj).otherSensor);
	}

	@Override
	public int hashCode() {
		return classHash(otherSensor, halfLife);
	}

	@Override
	public boolean isAvailable() {
		return otherSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "exponentialSmoothing(" + otherSensor + ", " + halfLife + ")";
	}
}
