/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class ConstantRealtimeRotation extends RealtimeRotation {

	private final Rotation value;

	ConstantRealtimeRotation(Rotation value) {
		this.value = value;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	protected Rotation calculateCheapValue() {
		return getConstantValue();
	}

	public Rotation getConstantValue() {
		return value;
	}

	@Override
	public RealtimeDouble getA() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(getConstantValue().getA()) : super.getA();
	}

	@Override
	public RealtimeDouble getB() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(getConstantValue().getB()) : super.getB();
	}

	@Override
	public RealtimeDouble getC() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(getConstantValue().getC()) : super.getC();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value.equals(((ConstantRealtimeRotation) obj).value);
	}

	@Override
	public int hashCode() {
		return classHash(value);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "rotation(A:" + value.getA() + ", B:" + value.getB() + ", C:" + value.getC() + ")";
	}

	@Override
	public RealtimeDouble getAngle() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(value.getAngle()) : super.getAngle();
	}

	@Override
	public RealtimeVector getAxis() {
		return CONSTANT_FOLDING ? RealtimeVector.createFromConstant(value.getAxis()) : super.getAxis();
	}

	@Override
	public RealtimeDouble getQuaternionW() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(value.getQuaternion().getW())
				: super.getQuaternionW();
	}

	@Override
	public RealtimeDouble getQuaternionX() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(value.getQuaternion().getX())
				: super.getQuaternionX();
	}

	@Override
	public RealtimeDouble getQuaternionY() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(value.getQuaternion().getY())
				: super.getQuaternionY();
	}

	@Override
	public RealtimeDouble getQuaternionZ() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(value.getQuaternion().getZ())
				: super.getQuaternionZ();
	}

	@Override
	public RealtimeRotation invert() {
		return CONSTANT_FOLDING ? createFromConstant(value.invert()) : super.invert();
	}

	@Override
	public RealtimeRotation multiply(Rotation rotation) {
		return CONSTANT_FOLDING ? createFromConstant(value.multiply(rotation)) : super.multiply(rotation);
	}

	@Override
	public RealtimeRotation multiply(RealtimeRotation rotation) {
		if (CONSTANT_FOLDING && value.isIdentityRotation()) {
			return rotation;
		} else if (CONSTANT_FOLDING && rotation.isConstant()) {
			return multiply(rotation.getCheapValue());
		} else {
			return super.multiply(rotation);
		}
	}
}
