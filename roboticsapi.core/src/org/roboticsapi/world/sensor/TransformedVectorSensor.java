/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;

public final class TransformedVectorSensor extends VectorSensor {

	private final VectorSensor otherSensor;
	private final TransformationSensor transformation;

	public TransformedVectorSensor(VectorSensor otherSensor, TransformationSensor transformation) {
		super(selectRuntime(otherSensor, transformation));
		addInnerSensors(otherSensor, transformation);
		this.otherSensor = otherSensor;
		this.transformation = transformation;
	}

	public TransformedVectorSensor(VectorSensor otherSensor, RotationSensor transformation) {
		super(selectRuntime(otherSensor, transformation));
		addInnerSensors(otherSensor, transformation);
		this.otherSensor = otherSensor;
		this.transformation = TransformationSensor.fromRotationSensor(transformation);
	}

	public VectorSensor getOtherSensor() {
		return otherSensor;
	}

	public TransformationSensor getTransformationSensor() {
		return transformation;
	}

	@Override
	protected Vector calculateCheapValue() {
		Transformation newToOld = getTransformationSensor().getCheapValue();
		Vector oldTranslation = getOtherSensor().getCheapValue();
		return (newToOld == null || oldTranslation == null) ? null : newToOld.apply(oldTranslation);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && otherSensor.equals(((TransformedVectorSensor) obj).otherSensor)
				&& transformation.equals(((TransformedVectorSensor) obj).transformation);
	}

	@Override
	public int hashCode() {
		return classHash(otherSensor, transformation);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(otherSensor, transformation);
	}

	@Override
	public String toString() {
		return "transform(" + transformation + ", " + otherSensor + ")";
	}
}
