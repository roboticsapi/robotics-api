/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.GetFromVectorRealtimeDouble.VectorComponent;

/**
 * A VectorSensor measures a three-dimensional vector (see {@link Vector}.
 */

public abstract class RealtimeVector extends RealtimeValue<Vector> {
	public static final RealtimeVector ZERO = new ConstantRealtimeVector(Vector.ZERO);

	@Override
	public final RealtimeVector substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeVector) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeVector performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	/**
	 * Instantiates a new VectorSensor for the supplied {@link RoboticsRuntime}.
	 *
	 * @param runtime the runtime of this sensor
	 */
	public RealtimeVector(RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Instantiates a new VectorSensor for any {@link RoboticsRuntime}.
	 */
	public RealtimeVector() {
		super();
	}

	/**
	 * Instantiates a new VectorSensor for the supplied {@link RealtimeValue}s.
	 *
	 * @param runtime the runtime of this sensor
	 */
	public RealtimeVector(RealtimeValue<?>... values) {
		super(values);
	}

	/**
	 * Gets a {@link RealtimeDouble} representing the x component of this
	 * VectorSensor.
	 *
	 * @return the x component sensor
	 */
	public RealtimeDouble getX() {
		return new GetFromVectorRealtimeDouble(this, VectorComponent.X);
	}

	/**
	 * Gets a {@link RealtimeDouble} representing the y component of this
	 * VectorSensor.
	 *
	 * @return the y component sensor
	 */
	public RealtimeDouble getY() {
		return new GetFromVectorRealtimeDouble(this, VectorComponent.Y);
	}

	/**
	 * Gets a {@link RealtimeDouble} representing the z component of this
	 * VectorSensor.
	 *
	 * @return the z component sensor
	 */
	public RealtimeDouble getZ() {
		return new GetFromVectorRealtimeDouble(this, VectorComponent.Z);
	}

	/**
	 * Gets a {@link RealtimeDouble} measuring the absolute length of the vector
	 * measured by this VectorSensor.
	 *
	 * @return the sensor measuring vector length
	 */
	public RealtimeDouble getLength() {
		return getX().square().add(getY().square()).add(getZ().square()).sqrt();
	}

	public RealtimeDouble getPhi() {
		return RealtimeDouble.createAtan2(getY(), getX());
	}

	public RealtimeDouble getTheta() {
		return RealtimeDouble.createAtan2(getZ(), getX().multiply(getX()).add(getY().multiply(getY())).sqrt())
				.multiply(-1).add(Math.PI / 2);
	}

	/**
	 * Scales the vector sensor
	 *
	 * @param factor scale factor
	 * @return this vector scaled by the given factor
	 */
	public RealtimeVector scale(RealtimeDouble factor) {
		return RealtimeVector.createFromXYZ(getX().multiply(factor), getY().multiply(factor), getZ().multiply(factor));
	}

	/**
	 * Creates a new VectorSensor that transforms the vector measured by this sensor
	 * with a {@link Transformation} measured by the given
	 * {@link RealtimeTransformation}.
	 *
	 * @param transformation the {@link RealtimeTransformation} used to transform
	 *                       this VectorSensor
	 * @return new VectorSensor measuring the transformed vector
	 */
	public RealtimeVector transform(RealtimeTransformation transformation) {
		if (transformation.isConstant() && transformation.getCheapValue().isIdentityTransformation()) {
			return this;
		}
		return new TransformedRealtimeVector(this, transformation);
	}

	/**
	 * Creates a new VectorSensor that transforms the vector measured by this sensor
	 * with a {@link Rotation} measured by the given {@link RealtimeRotation}.
	 *
	 * @param rotation the {@link RealtimeRotation} used to transform this
	 *                 VectorSensor
	 * @return new VectorSensor measuring the transformed vector
	 */
	public RealtimeVector transform(RealtimeRotation rotation) {
		return new RotatedRealtimeVector(rotation, this);
	}

	/**
	 * Creates a new VectorSensor that transforms the vector measured by this sensor
	 * with a {@link Vector} measured by the given {@link RealtimeVector} (i.e. adds
	 * this vector to the vector measured by the given sensor).
	 *
	 * @param vector the {@link RealtimeVector} used to transform this VectorSensor
	 * @return new VectorSensor measuring the transformed vector
	 */
	public RealtimeVector transform(RealtimeVector vector) {
		return transform(RealtimeTransformation.createFromVector(vector));
	}

	/**
	 * Creates a new VectorSensor from three independent {@link RealtimeDouble}s
	 * measuring the vector's single components.
	 *
	 * @param xComponent sensor measuring the x component
	 * @param yComponent sensor measuring the y component
	 * @param zComponent sensor measuring the z component
	 * @return new VectorSensor measuring a vector combined from the given
	 *         components
	 */
	public static RealtimeVector createFromXYZ(RealtimeDouble xComponent, RealtimeDouble yComponent,
			RealtimeDouble zComponent) {
		if (CONSTANT_FOLDING && xComponent.isConstant() && yComponent.isConstant() && zComponent.isConstant()) {
			return createFromConstant(
					new Vector(xComponent.getCheapValue(), yComponent.getCheapValue(), zComponent.getCheapValue()));
		} else {
			return new XYZToRealtimeVector(xComponent, yComponent, zComponent);
		}
	}

	/**
	 * Creates a new {@link RealtimeVector} that represent a constant value.
	 *
	 * @param vector the vector the new {@link RealtimeVector} should constantly
	 *               represent
	 * @return new VectorSensor measuring the given constant vector
	 */
	public static RealtimeVector createFromConstant(Vector vector) {
		return new ConstantRealtimeVector(vector);
	}

	/**
	 * Adds a {@link Vector} to this {@link RealtimeVector}.
	 *
	 * @param other other vector
	 * @return {@code this + other}
	 */
	public RealtimeVector add(Vector other) {
		return add(createFromConstant(other));
	}

	/**
	 * Adds another {@link RealtimeVector} to this {@link RealtimeVector}.
	 *
	 * @param other other vector
	 * @return {@code this + other}
	 */
	public RealtimeVector add(RealtimeVector other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.add(this);
		} else {
			return new AddedRealtimeVector(this, other);
		}
	}

	/**
	 * Calculates the dot product of this {@link RealtimeVector} with another
	 * {@link RealtimeVector}.
	 *
	 * @param other the other RealtimeVector
	 * @return {@code this * other}
	 */
	public RealtimeDouble dot(RealtimeVector other) {
		return getX().multiply(other.getX()).add(getY().multiply(other.getY())).add(getZ().multiply(other.getZ()));

	}

	/**
	 * Calculates the cross product of a {@link Vector} with this
	 * {@link RealtimeVector}.
	 *
	 * @param other other vector
	 * @return {@code this x other}
	 */
	public RealtimeVector cross(Vector other) {
		return cross(createFromConstant(other));
	}

	/**
	 * Calculate the cross product of another {@link RealtimeVector} with this
	 * {@link RealtimeVector}.
	 *
	 * @param other other vector
	 * @return {@code this x other}
	 */
	public RealtimeVector cross(RealtimeVector other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.cross(this);
		}
		return new CrossProductRealtimeVector(this, other);
	}

	/**
	 * Returns a new VectorSensor which is the inverse of this VectorSensor (i.e.,
	 * -1 * (this vector)).
	 *
	 * @return inverse VectorSensor
	 */
	public RealtimeVector invert() {
		return scale(RealtimeDouble.createFromConstant(-1d));
	}

	/**
	 * Returns a sliding average of the vector over the given duration
	 *
	 * @param duration duration of the average
	 * @return Vector sensor that contains the smoothed value
	 */
	public RealtimeVector slidingAverage(double duration) {
		return createFromXYZ(getX().slidingAverage(duration), getY().slidingAverage(duration),
				getZ().slidingAverage(duration));
	}

	@Override
	public RealtimeVector fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeVectorAtTime(this, age, maxAge);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeVectorIsNull(this);
	}

}
