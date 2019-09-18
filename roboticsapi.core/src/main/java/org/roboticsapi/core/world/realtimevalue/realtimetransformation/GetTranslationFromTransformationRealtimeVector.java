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
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class GetTranslationFromTransformationRealtimeVector extends RealtimeVector {

	@Override
	protected RealtimeVector performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new GetTranslationFromTransformationRealtimeVector(transformation.substitute(substitutionMap));
	}

	private final RealtimeTransformation transformation;

	GetTranslationFromTransformationRealtimeVector(RealtimeTransformation transformation) {
		super(transformation);
		this.transformation = transformation;
	}

	public RealtimeTransformation getTransformation() {
		return transformation;
	}

	@Override
	protected Vector calculateCheapValue() {
		Transformation cheapValue = getTransformation().getCheapValue();
		return cheapValue == null ? null : cheapValue.getTranslation();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj)
				&& transformation.equals(((GetTranslationFromTransformationRealtimeVector) obj).transformation);
	}

	@Override
	public int hashCode() {
		return classHash(transformation);
	}

	@Override
	public boolean isAvailable() {
		return transformation.isAvailable();
	}

	@Override
	public String toString() {
		return "translation(" + transformation + ")";
	}
}
