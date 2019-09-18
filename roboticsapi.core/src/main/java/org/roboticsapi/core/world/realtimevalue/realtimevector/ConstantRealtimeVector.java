/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;

/**
 * This class represents a constant {@link Vector} value available in real-time
 * in an execution environment.
 *
 * @see RealtimeValue
 * @see RealtimeVector
 * @see RoboticsRuntime
 */
public final class ConstantRealtimeVector extends RealtimeVector {

	/**
	 * The constant double vector
	 */
	private final Vector value;

	/**
	 * Constructs a new constant {@link RealtimeVector}.
	 *
	 * @param value vector which will be constant over time
	 */
	ConstantRealtimeVector(Vector value) {
		this.value = value;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	protected Vector calculateCheapValue() {
		return getConstantValue();
	}

	/**
	 * Returns the constant vector.
	 *
	 * @return the constant value
	 */
	public Vector getConstantValue() {
		return value;
	}

	@Override
	public RealtimeDouble getX() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(getConstantValue().getX()) : super.getX();
	}

	@Override
	public RealtimeDouble getY() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(getConstantValue().getY()) : super.getY();
	}

	@Override
	public RealtimeDouble getZ() {
		return CONSTANT_FOLDING ? RealtimeDouble.createFromConstant(getConstantValue().getZ()) : super.getZ();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value.equals(((ConstantRealtimeVector) obj).value);
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
		return "vector(" + value.getX() + ", " + value.getY() + ", " + value.getZ() + ")";
	}

	@Override
	public RealtimeVector add(Vector other) {
		return CONSTANT_FOLDING ? createFromConstant(value.add(other)) : super.add(other);
	}

	@Override
	public RealtimeVector add(RealtimeVector other) {
		if (CONSTANT_FOLDING && value.isNullVector()) {
			return other;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return add(other.getCheapValue());
		} else {
			return super.add(other);
		}
	}

	@Override
	public RealtimeVector scale(RealtimeDouble factor) {
		if (CONSTANT_FOLDING && factor.isConstant()) {
			return createFromConstant(value.scale(factor.getCheapValue()));
		} else {
			return super.scale(factor);
		}
	}

	@Override
	public RealtimeVector cross(Vector v) {
		return CONSTANT_FOLDING ? createFromConstant(value.cross(v)) : super.cross(v);
	}

	@Override
	public RealtimeVector cross(RealtimeVector other) {
		if (CONSTANT_FOLDING && value.isNullVector()) {
			return this;
		} else if (CONSTANT_FOLDING && other.isConstant()) {
			return cross(other.getCheapValue());
		} else {
			return super.cross(other);
		}
	}

	@Override
	public RealtimeVector transform(RealtimeTransformation transformation) {
		if (CONSTANT_FOLDING && transformation.isConstant()) {
			return createFromConstant(transformation.getCheapValue().apply(value));
		} else {
			return super.transform(transformation);
		}
	}

}
