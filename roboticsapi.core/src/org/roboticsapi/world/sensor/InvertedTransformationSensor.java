/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Transformation;

public final class InvertedTransformationSensor extends TransformationSensor {

	private final TransformationSensor otherSensor;

	public InvertedTransformationSensor(TransformationSensor otherSensor) {
		super(selectRuntime(otherSensor));
		addInnerSensors(otherSensor);
		this.otherSensor = otherSensor;
	}

	@Override
	protected Transformation calculateCheapValue() {
		Transformation cheapValue = otherSensor.getCheapValue();
		return cheapValue == null ? null : cheapValue.invert();
	}

	@Override
	public TransformationSensor invert() {
		return otherSensor;
	}

	public TransformationSensor getOtherSensor() {
		return otherSensor;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && otherSensor.equals(((InvertedTransformationSensor) obj).otherSensor);
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
		return "((" + otherSensor + ") ^ -1 )";
	}
}
