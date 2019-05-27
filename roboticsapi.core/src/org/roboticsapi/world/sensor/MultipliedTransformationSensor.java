/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Transformation;

/**
 * The MultipliedTransformationSensor represents the multiplied value of two
 * TransformationSensors.
 */
public final class MultipliedTransformationSensor extends TransformationSensor {

	private final TransformationSensor first;
	private final TransformationSensor second;

	/**
	 * Instantiates a new MultipliedTransformationSensor.
	 *
	 * @param first  the first multiplication argument
	 * @param second the second multiplication argument
	 */
	public MultipliedTransformationSensor(TransformationSensor first, TransformationSensor second) {
		super(selectRuntime(first, second));
		addInnerSensors(first, second);
		this.first = first;
		this.second = second;
	}

	@Override
	protected Transformation calculateCheapValue() {

		Transformation cheap1 = first.getCheapValue();
		Transformation cheap2 = second.getCheapValue();
		return (cheap1 == null || cheap2 == null) ? null : cheap1.multiply(cheap2);
	}

	/**
	 * Gets the first transformation.
	 *
	 * @return the first transformation
	 */
	public TransformationSensor getFirstTransformation() {
		return first;
	}

	/**
	 * Gets the second transformation.
	 *
	 * @return the second transformation
	 */
	public TransformationSensor getSecondTransformation() {
		return second;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && first.equals(((MultipliedTransformationSensor) obj).first)
				&& second.equals(((MultipliedTransformationSensor) obj).second);
	}

	@Override
	public int hashCode() {
		return classHash(first, second);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(first, second);
	}

	@Override
	public String toString() {
		return "(" + first + " * " + second + ")";
	}
}
