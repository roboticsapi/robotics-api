/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Transformation;

/**
 * A sensor observing a static connection
 */
public final class ConstantTransformationSensor extends TransformationSensor {
	private final Transformation transformation;

	public ConstantTransformationSensor(Transformation transformation) {
		super(null);
		this.transformation = transformation;
	}

	@Override
	protected Transformation calculateCheapValue() {
		return transformation;
	}

	public Transformation getTransformation() {
		return transformation;
	}

	@Override
	public VectorSensor getTranslationSensor() {
		return VectorSensor.fromConstant(getTransformation().getTranslation());
	}

	@Override
	public RotationSensor getRotationSensor() {
		return RotationSensor.fromConstant(getTransformation().getRotation());
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && transformation.equals(((ConstantTransformationSensor) obj).transformation);
	}

	@Override
	public int hashCode() {
		return classHash(transformation);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return transformation.toString();
	}

	@Override
	public synchronized void addListener(SensorListener<Transformation> listener) throws RoboticsException {
		listener.onValueChanged(transformation);
	}

}
