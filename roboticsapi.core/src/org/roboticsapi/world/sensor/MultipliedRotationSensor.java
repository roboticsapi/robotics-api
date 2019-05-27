/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

public final class MultipliedRotationSensor extends RotationSensor {

	private final RotationSensor otherSensor;
	private final RotationSensor rotationSensor;

	public MultipliedRotationSensor(RotationSensor otherSensor, RotationSensor rotationSensor) {
		super(selectRuntime(otherSensor));
		addInnerSensors(otherSensor);
		this.otherSensor = otherSensor;
		this.rotationSensor = rotationSensor;
	}

	public RotationSensor getOtherSensor() {
		return otherSensor;
	}

	public RotationSensor getRotationSensor() {
		return rotationSensor;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && rotationSensor.equals(((MultipliedRotationSensor) obj).rotationSensor)
				&& otherSensor.equals(((MultipliedRotationSensor) obj).otherSensor);
	}

	@Override
	public int hashCode() {
		return classHash(rotationSensor, otherSensor);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(otherSensor, rotationSensor);
	}

	@Override
	public String toString() {
		return "(" + rotationSensor + " * " + otherSensor + ")";
	}
}
