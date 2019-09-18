/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public abstract class RealtimeTwist extends RealtimeValue<Twist> {

	@Override
	public final RealtimeTwist substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeTwist) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeTwist performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	public RealtimeTwist(RoboticsRuntime runtime) {
		super(runtime);
	}

	public RealtimeTwist(RealtimeValue<?>... values) {
		super(values);
	}

	public RealtimeTwist add(RealtimeTwist other) {
		if (CONSTANT_FOLDING && other.isConstant() && other.getCheapValue().getRotVel().getLength() == 0
				&& other.getCheapValue().getTransVel().getLength() == 0) {
			return this;
		}
		return new AddedRealtimeTwist(this, other);
	}

	public RealtimeVector getTranslationVelocity() {
		return new GetLinearVelocityFromTwistRealtimeVector(this);
	}

	public RealtimeVector getRotationVelocity() {
		return new GetAngularVelocityFromTwistRealtimeVector(this);
	}

	public RealtimeTwist invert() {
		return new InvertedRealtimeTwist(this);
	}

	public RealtimeTwist changePivotPoint(RealtimeVector pivotChange) {
		if (pivotChange.isConstant() && pivotChange.equals(RealtimeVector.ZERO)) {
			return this;
		}
		return new PivotAdaptedRealtimeTwist(this, pivotChange);
	}

	public RealtimeTwist changeOrientation(RealtimeRotation orientationChange) {
		if (orientationChange.isConstant() && orientationChange.equals(RealtimeRotation.IDENTITY)) {
			return this;
		}
		return new OrientationAdaptedRealtimeTwist(this, orientationChange);
	}

	public static RealtimeTwist createFromConstant(Twist value) {
		return new ConstantRealtimeTwist(value);
	}

	public static RealtimeTwist createFromLinearAngular(RealtimeDouble vx, RealtimeDouble vy, RealtimeDouble vz,
			RealtimeDouble omegaX, RealtimeDouble omegaY, RealtimeDouble omegaZ) {
		return createFromLinearAngular(RealtimeVector.createFromXYZ(vx, vy, vz),
				RealtimeVector.createFromXYZ(omegaX, omegaY, omegaZ));
	}

	public static RealtimeTwist createFromLinearAngular(RealtimeVector translationVelocity,
			RealtimeVector rotationVelocity) {
		return new VectorToRealtimeTwist(translationVelocity, rotationVelocity);
	}

	public static RealtimeTwist createConditional(RealtimeBoolean condition, RealtimeTwist ifTrue,
			RealtimeTwist ifFalse) {
		return new ConditionalRealtimeTwist(condition, ifTrue, ifFalse);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeTwistIsNull(this);
	}

	@Override
	public RealtimeTwist fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeTwistAtTime(this, age, maxAge);
	}

	public RealtimeTransformation integrate(RealtimeTransformation start) {
		return new RealtimeTwistToRealtimeTransformation(start, this);
	}
}
