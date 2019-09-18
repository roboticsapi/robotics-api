/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Transformation;

/**
 * The MultipliedTransformationSensor represents the multiplied value of two
 * TransformationSensors.
 */
public final class MultipliedRealtimeTransformation extends RealtimeTransformation {

	@Override
	protected RealtimeTransformation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new MultipliedRealtimeTransformation(first.substitute(substitutionMap),
				second.substitute(substitutionMap));
	}

	private final RealtimeTransformation first;
	private final RealtimeTransformation second;

	/**
	 * Instantiates a new MultipliedTransformationSensor.
	 *
	 * @param first  the first multiplication argument
	 * @param second the second multiplication argument
	 */
	MultipliedRealtimeTransformation(RealtimeTransformation first, RealtimeTransformation second) {
		super(first, second);
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
	public RealtimeTransformation getFirstTransformation() {
		return first;
	}

	/**
	 * Gets the second transformation.
	 *
	 * @return the second transformation
	 */
	public RealtimeTransformation getSecondTransformation() {
		return second;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && first.equals(((MultipliedRealtimeTransformation) obj).first)
				&& second.equals(((MultipliedRealtimeTransformation) obj).second);
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

	@Override
	public RealtimeTransformation multiply(RealtimeTransformation other) {
		if (CONSTANT_FOLDING && other.isConstant() && second.isConstant()) {
			return getFirstTransformation().multiply(second.multiply(other));
		}
		return super.multiply(other);
	}
}
