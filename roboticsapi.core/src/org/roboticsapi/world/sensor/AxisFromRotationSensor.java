/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;

public final class AxisFromRotationSensor extends VectorSensor {

	private final RotationSensor rotationSensor;

	public AxisFromRotationSensor(RotationSensor rotationSensor) {
		super(selectRuntime(rotationSensor));
		addInnerSensors(rotationSensor);
		this.rotationSensor = rotationSensor;
	}

	public RotationSensor getRotationSensor() {
		return rotationSensor;
	}

	@Override
	protected Vector calculateCheapValue() {
		Rotation rotation = this.rotationSensor.getCheapValue();

		if (rotation == null) {
			return null;
		}
		return rotation.getAxis();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && rotationSensor.equals(((AxisFromRotationSensor) obj).rotationSensor);
	}

	@Override
	public int hashCode() {
		return classHash(rotationSensor);
	}

	@Override
	public boolean isAvailable() {
		return rotationSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "axis(" + rotationSensor + ")";
	}
}
