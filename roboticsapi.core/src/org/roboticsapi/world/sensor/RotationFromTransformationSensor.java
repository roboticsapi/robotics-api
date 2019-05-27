/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;

public final class RotationFromTransformationSensor extends RotationSensor {

	private final TransformationSensor transformationSensor;

	public RotationFromTransformationSensor(TransformationSensor transformationSensor) {
		super(selectRuntime(transformationSensor));
		addInnerSensors(transformationSensor);
		this.transformationSensor = transformationSensor;
	}

	public TransformationSensor getTransformationSensor() {
		return transformationSensor;
	}

	@Override
	protected Rotation calculateCheapValue() {
		Transformation cheapValue = getTransformationSensor().getCheapValue();
		return cheapValue == null ? null : cheapValue.getRotation();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj)
				&& transformationSensor.equals(((RotationFromTransformationSensor) obj).transformationSensor);
	}

	@Override
	public int hashCode() {
		return classHash(transformationSensor);
	}

	@Override
	public boolean isAvailable() {
		return transformationSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "rotation(" + transformationSensor + ")";
	}
}
