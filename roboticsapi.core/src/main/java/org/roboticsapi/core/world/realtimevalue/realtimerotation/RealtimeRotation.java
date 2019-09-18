/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.GetFromRotationRealtimeDouble.RotationComponent;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public abstract class RealtimeRotation extends RealtimeValue<Rotation> {
	public static final RealtimeRotation IDENTITY = new ConstantRealtimeRotation(Rotation.IDENTITY);

	@Override
	public final RealtimeRotation substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeRotation) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeRotation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	public RealtimeRotation(RoboticsRuntime runtime) {
		super(runtime);
	}

	public RealtimeRotation(RealtimeValue<?>... values) {
		super(values);
	}

	public RealtimeDouble getA() {
		return new GetFromRotationRealtimeDouble(this, RotationComponent.A);
	}

	public RealtimeDouble getB() {
		return new GetFromRotationRealtimeDouble(this, RotationComponent.B);
	}

	public RealtimeDouble getC() {
		return new GetFromRotationRealtimeDouble(this, RotationComponent.C);
	}

	public RealtimeDouble getQuaternionX() {
		return new GetFromRotationRealtimeDouble(this, RotationComponent.QuaternionX);
	}

	public RealtimeDouble getQuaternionY() {
		return new GetFromRotationRealtimeDouble(this, RotationComponent.QuaternionY);
	}

	public RealtimeDouble getQuaternionZ() {
		return new GetFromRotationRealtimeDouble(this, RotationComponent.QuaternionZ);
	}

	public RealtimeDouble getQuaternionW() {
		return new GetFromRotationRealtimeDouble(this, RotationComponent.QuaternionW);
	}

	public RealtimeRotation multiply(RealtimeRotation rotation) {
		if (CONSTANT_FOLDING && rotation.isConstant() && rotation.getCheapValue().isIdentityRotation()) {
			return this;
		} else {
			return new MultipliedRealtimeRotation(this, rotation);
		}
	}

	public RealtimeRotation multiply(Rotation rotation) {
		if (CONSTANT_FOLDING && rotation.isIdentityRotation()) {
			return this;
		} else {
			return new MultipliedRealtimeRotation(this, RealtimeRotation.createFromConstant(rotation));
		}
	}

	public RealtimeRotation multiply(RealtimeTransformation transformation) {
		if (CONSTANT_FOLDING && transformation.isConstant()
				&& transformation.getCheapValue().isIdentityTransformation()) {
			return this;
		} else {
			return multiply(transformation.getRotation());
		}
	}

	public RealtimeVector getAxis() {
		return new GetAxisFromRotationRealtimeVector(this);
	}

	public RealtimeDouble getAngle() {
		return new GetAngleFromRotationRealtimeDouble(this);
	}

	public RealtimeBoolean equals(RealtimeRotation other, double delta) {
		return other.invert().multiply(this).getAngle().equals(0, delta);
	}

	public RealtimeRotation invert() {
		return new InvertedRealtimeRotation(this);
	}

	public static RealtimeRotation createFromABC(RealtimeDouble aComponent, RealtimeDouble bComponent,
			RealtimeDouble cComponent) {
		if (CONSTANT_FOLDING && aComponent.isConstant() && bComponent.isConstant() && cComponent.isConstant()) {
			return createFromConstant(
					new Rotation(aComponent.getCheapValue(), bComponent.getCheapValue(), cComponent.getCheapValue()));
		} else {
			return new ABCToRealtimeRotation(aComponent, bComponent, cComponent);
		}
	}

	public static RealtimeRotation createFromConstant(Rotation orientation) {
		if (orientation.isIdentityRotation()) {
			return IDENTITY;
		} else {
			return new ConstantRealtimeRotation(orientation);
		}
	}

	public static RealtimeRotation createFromAxisAngle(RealtimeVector axis, RealtimeDouble angle) {
		return new AxisAngleToRealtimeRotation(axis, angle);
	}

	public static RealtimeRotation createFromAxisAngle(Vector axis, RealtimeDouble angle) {
		return new AxisAngleToRealtimeRotation(RealtimeVector.createFromConstant(axis), angle);
	}

	public static RealtimeRotation createFromQuaternion(RealtimeDouble x, RealtimeDouble y, RealtimeDouble z,
			RealtimeDouble w) {
		return new QuaternionToRealtimeRotation(x, y, z, w);
	}

	public RealtimeRotation slidingAverage(double duration) {
		return new SlidingAverageRealtimeRotation(this, duration);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeRotationIsNull(this);
	}

	@Override
	public RealtimeRotation fromHistory(RealtimeDouble age, double maxAge) {
		// FIXME: implement
		return null;
	}

}
