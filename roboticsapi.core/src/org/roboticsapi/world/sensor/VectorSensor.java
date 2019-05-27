/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.VectorComponentSensor.VectorComponent;

/**
 * A VectorSensor measures a three-dimensional vector (see {@link Vector}.
 */

public abstract class VectorSensor extends Sensor<Vector> {
	/**
	 * Instantiates a new VectorSensor for the supplied {@link RoboticsRuntime}.
	 *
	 * @param runtime the runtime of this sensor
	 */
	public VectorSensor(RoboticsRuntime runtime) {
		super(runtime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.sensor.Sensor#getDefaultValue()
	 */
	@Override
	protected Vector getDefaultValue() {
		return Vector.getNullVector();
	}

	/**
	 * Gets a {@link DoubleSensor} representing the x component of this
	 * VectorSensor.
	 *
	 * @return the x component sensor
	 */
	public DoubleSensor getXSensor() {
		return new VectorComponentSensor(this, VectorComponent.X);
	}

	/**
	 * Gets a {@link DoubleSensor} representing the y component of this
	 * VectorSensor.
	 *
	 * @return the y component sensor
	 */
	public DoubleSensor getYSensor() {
		return new VectorComponentSensor(this, VectorComponent.Y);
	}

	/**
	 * Gets a {@link DoubleSensor} representing the z component of this
	 * VectorSensor.
	 *
	 * @return the z component sensor
	 */
	public DoubleSensor getZSensor() {
		return new VectorComponentSensor(this, VectorComponent.Z);
	}

	/**
	 * Gets a {@link DoubleSensor} measuring the absolute length of the vector
	 * measured by this VectorSensor.
	 *
	 * @return the sensor measuring vector length
	 */
	public DoubleSensor getLengthSensor() {
		return getXSensor().square().add(getYSensor().square()).add(getZSensor().square()).sqrt();
	}

	public DoubleSensor getPhiSensor() {
		return DoubleSensor.atan2(getYSensor(), getXSensor());
	}

	public DoubleSensor getThetaSensor() {
		return DoubleSensor
				.atan2(getZSensor(),
						getXSensor().multiply(getXSensor()).add(getYSensor().multiply(getYSensor())).sqrt())
				.multiply(-1).add(Math.PI / 2);
	}

	/**
	 * Scales the vector sensor
	 *
	 * @param factor scale factor
	 * @return this vector scaled by the given factor
	 */
	public VectorSensor scale(DoubleSensor factor) {
		return VectorSensor.fromComponents(getXSensor().multiply(factor), getYSensor().multiply(factor),
				getZSensor().multiply(factor));
	}

	/**
	 * Creates a new VectorSensor that transforms the vector measured by this sensor
	 * with a {@link Transformation} measured by the given
	 * {@link TransformationSensor}.
	 *
	 * @param sensor the {@link TransformationSensor} used to transform this
	 *               VectorSensor
	 * @return new VectorSensor measuring the transformed vector
	 */
	public VectorSensor transform(TransformationSensor sensor) {
		return new TransformedVectorSensor(this, sensor);
	}

	/**
	 * Creates a new VectorSensor that transforms the vector measured by this sensor
	 * with a {@link Rotation} measured by the given {@link RotationSensor}.
	 *
	 * @param sensor the {@link RotationSensor} used to transform this VectorSensor
	 * @return new VectorSensor measuring the transformed vector
	 */
	public VectorSensor transform(RotationSensor sensor) {
		return transform(TransformationSensor.fromRotationSensor(sensor));
	}

	/**
	 * Creates a new VectorSensor that transforms the vector measured by this sensor
	 * with a {@link Vector} measured by the given {@link VectorSensor} (i.e. adds
	 * this vector to the vector measured by the given sensor).
	 *
	 * @param sensor the {@link VectorSensor} used to transform this VectorSensor
	 * @return new VectorSensor measuring the transformed vector
	 */
	public VectorSensor transform(VectorSensor sensor) {
		return transform(TransformationSensor.fromVectorSensor(sensor));
	}

	/**
	 * Creates a new VectorSensor from three independent {@link DoubleSensor}s
	 * measuring the vector's single components.
	 *
	 * @param xComponent sensor measuring the x component
	 * @param yComponent sensor measuring the y component
	 * @param zComponent sensor measuring the z component
	 * @return new VectorSensor measuring a vector combined from the given
	 *         components
	 */
	public static VectorSensor fromComponents(DoubleSensor xComponent, DoubleSensor yComponent,
			DoubleSensor zComponent) {
		return new VectorFromComponentsSensor(xComponent, yComponent, zComponent);
	}

	/**
	 * Creates a new VectorSensor that measures a constant value.
	 *
	 * @param vector the vector the new sensor should constantly measure
	 * @return new VectorSensor measuring the given constant vector
	 */
	public static VectorSensor fromConstant(Vector vector) {
		return new ConstantVectorSensor(vector);
	}

	/**
	 * Adds another vector sensor to this vector sensor
	 *
	 * @param other other vector sensor
	 * @return sum of this and the other vector sensor
	 */
	public VectorSensor add(VectorSensor other) {
		return VectorSensor.fromComponents(this.getXSensor().add(other.getXSensor()),
				this.getYSensor().add(other.getYSensor()), this.getZSensor().add(other.getZSensor()));
	}

	public VectorSensor cross(Vector v) {
		return cross(fromConstant(v));
	}

	public VectorSensor cross(VectorSensor other) {
		// a2*b3 - a3*b2
		DoubleSensor c1 = this.getYSensor().multiply(other.getZSensor())
				.add(this.getZSensor().multiply(other.getYSensor()).negate());
		// a3*b1 - a1*b3
		DoubleSensor c2 = this.getZSensor().multiply(other.getXSensor())
				.add(this.getXSensor().multiply(other.getZSensor()).negate());
		// a1*b2 - a2*b1
		DoubleSensor c3 = this.getXSensor().multiply(other.getYSensor())
				.add(this.getYSensor().multiply(other.getXSensor()).negate());

		return VectorSensor.fromComponents(c1, c2, c3);
	}

	/**
	 * Adds another vector to this vector sensor
	 *
	 * @param other other vector
	 * @return sum of this and the other vector
	 */
	public VectorSensor add(Vector other) {
		return add(VectorSensor.fromConstant(other));
	}

	/**
	 * Returns a new VectorSensor which is the inverse of this VectorSensor (i.e.,
	 * -1 * (this vector)).
	 *
	 * @return inverse VectorSensor
	 */
	public VectorSensor invert() {
		return scale(DoubleSensor.fromValue(-1d));
	}

	/**
	 * Returns a sliding average of the vector over the given duration
	 *
	 * @param duration duration of the average
	 * @return Vector sensor that contains the smoothed value
	 */
	public VectorSensor slidingAverage(double duration) {
		return fromComponents(getXSensor().slidingAverage(duration), getYSensor().slidingAverage(duration),
				getZSensor().slidingAverage(duration));
	}

}
