/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;

public final class TransformedRealtimeVector extends RealtimeVector {

	private final RealtimeVector vector;
	private final RealtimeTransformation transformation;

	TransformedRealtimeVector(RealtimeVector vector, RealtimeTransformation transformation) {
		super(vector, transformation);
		this.vector = vector;
		this.transformation = transformation;
	}

	TransformedRealtimeVector(RealtimeVector vector, RealtimeRotation rotation) {
		super(vector, rotation);
		this.vector = vector;
		this.transformation = RealtimeTransformation.createFromRotation(rotation);
	}

	public RealtimeVector getVector() {
		return vector;
	}

	public RealtimeTransformation getTransformation() {
		return transformation;
	}

	@Override
	protected Vector calculateCheapValue() {
		Transformation newToOld = getTransformation().getCheapValue();
		Vector oldTranslation = getVector().getCheapValue();
		return (newToOld == null || oldTranslation == null) ? null : newToOld.apply(oldTranslation);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && vector.equals(((TransformedRealtimeVector) obj).vector)
				&& transformation.equals(((TransformedRealtimeVector) obj).transformation);
	}

	@Override
	public int hashCode() {
		return classHash(vector, transformation);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(vector, transformation);
	}

	@Override
	public String toString() {
		return "transform(" + transformation + ", " + vector + ")";
	}
}
