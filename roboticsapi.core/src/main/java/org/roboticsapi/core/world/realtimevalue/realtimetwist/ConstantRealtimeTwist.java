/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * A velocity sensor for a constant velocity
 */
public final class ConstantRealtimeTwist extends RealtimeTwist {

	private final Twist constantValue;

	ConstantRealtimeTwist(Twist constantValue) {
		this.constantValue = constantValue;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	public Twist getConstantValue() {
		return constantValue;
	}

	@Override
	protected Twist calculateCheapValue() {
		return constantValue;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && constantValue.equals(((ConstantRealtimeTwist) obj).constantValue);
	}

	@Override
	public int hashCode() {
		return classHash(constantValue);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return constantValue.toString();
	}

	@Override
	public RealtimeTwist changeOrientation(RealtimeRotation orientationChange) {
		if (CONSTANT_FOLDING && constantValue.getRotVel().getLength() == 0
				&& constantValue.getTransVel().getLength() == 0) {
			return this;
		}
		if (CONSTANT_FOLDING && orientationChange.isConstant()) {
			return createFromConstant(constantValue.changeOrientation(orientationChange.getCheapValue()));
		}
		return super.changeOrientation(orientationChange);
	}

	@Override
	public RealtimeTwist changePivotPoint(RealtimeVector pivotChange) {
		if (CONSTANT_FOLDING && constantValue.getRotVel().getLength() == 0
				&& constantValue.getTransVel().getLength() == 0) {
			return this;
		}
		if (CONSTANT_FOLDING && pivotChange.isConstant()) {
			return createFromConstant(constantValue.changePivot(pivotChange.getCheapValue()));
		}
		return super.changePivotPoint(pivotChange);
	}

	@Override
	public RealtimeTwist add(RealtimeTwist other) {
		if (CONSTANT_FOLDING && constantValue.getRotVel().getLength() == 0
				&& constantValue.getTransVel().getLength() == 0) {
			return other;
		}
		if (CONSTANT_FOLDING && other.isConstant()) {
			return createFromConstant(constantValue.add(other.getCheapValue()));
		}
		return super.add(other);
	}

	@Override
	public RealtimeTwist invert() {
		return createFromConstant(new Twist(constantValue.getTransVel().invert(), constantValue.getRotVel().invert()));
	}

	@Override
	public RealtimeVector getTranslationVelocity() {
		if (CONSTANT_FOLDING) {
			return RealtimeVector.createFromConstant(constantValue.getTransVel());
		}
		return super.getTranslationVelocity();
	}

	@Override
	public RealtimeVector getRotationVelocity() {
		if (CONSTANT_FOLDING) {
			return RealtimeVector.createFromConstant(constantValue.getRotVel());
		}
		return super.getRotationVelocity();
	}

}
