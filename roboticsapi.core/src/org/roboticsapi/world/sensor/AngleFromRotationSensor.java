/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.UnaryFunctionDoubleSensor;
import org.roboticsapi.world.Rotation;

public final class AngleFromRotationSensor extends UnaryFunctionDoubleSensor<Rotation> {

	public AngleFromRotationSensor(RotationSensor rotationSensor) {
		super(rotationSensor);
	}

	public RotationSensor getRotationSensor() {
		return (RotationSensor) getInnerSensor();
	}

	@Override
	public String toString() {
		return "angle(" + getRotationSensor() + ")";
	}

	@Override
	protected Double computeCheapValue(Rotation value) {
		return value.getAngle();
	}
}
