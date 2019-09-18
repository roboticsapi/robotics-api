/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * A sensor observing a static connection
 */
public final class ConstantRealtimeTransformation extends RealtimeTransformation {
	private final Transformation transformation;

	ConstantRealtimeTransformation(Transformation transformation) {
		if (transformation == null) {
			throw new IllegalArgumentException("transformation may not be null");
		}
		this.transformation = transformation;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	protected Transformation calculateCheapValue() {
		return transformation;
	}

	public Transformation getTransformation() {
		return transformation;
	}

	@Override
	public RealtimeVector getTranslation() {
		return RealtimeVector.createFromConstant(getTransformation().getTranslation());
	}

	@Override
	public RealtimeRotation getRotation() {
		return RealtimeRotation.createFromConstant(getTransformation().getRotation());
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && transformation.equals(((ConstantRealtimeTransformation) obj).transformation);
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
	public RealtimeTransformation invert() {
		return CONSTANT_FOLDING ? createfromConstant(transformation.invert()) : super.invert();
	}

	@Override
	public RealtimeTransformation multiply(RealtimeTransformation other) {
		if (CONSTANT_FOLDING && transformation.isIdentityTransformation()) {
			return other;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return createfromConstant(transformation.multiply(other.getCheapValue()));
		} else {
			return super.multiply(other);
		}
	}
}
