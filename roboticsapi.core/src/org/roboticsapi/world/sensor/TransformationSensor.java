/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;

/**
 * A Robotics API {@link Sensor} measuring {@link Transformation} values.
 */
public abstract class TransformationSensor extends Sensor<Transformation> {

	/**
	 * Creates a new TransformationSensor that is valid for a given
	 * {@link RoboticsRuntime}.
	 * 
	 * @param runtime the runtime
	 */
	public TransformationSensor(RoboticsRuntime runtime) {
		super(runtime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.Sensor#getDefaultValue()
	 */
	@Override
	protected Transformation getDefaultValue() {
		return new Transformation();
	}

	/**
	 * Creates a new {@link TransformationSensor} which delivers the inverted
	 * {@link Transformation} values that are measured by this TransformationSensor.
	 * 
	 * @return the inverted TransformationSensor
	 */
	public TransformationSensor invert() {
		return new InvertedTransformationSensor(this);
	}

	/**
	 * Creates a new {@link TransformationSensor} which delivers the values of this
	 * TransformationSensor, multiplied with the values delivered by a given
	 * TransformationSensor.
	 * 
	 * @param other the other TransformationSensor, where this
	 *              TransformationSensor's values are multiplied with the values of
	 *              the other TransformationSensor
	 * @return TransformationSensor delivering the result values of the
	 *         multiplication
	 */
	public TransformationSensor multiply(TransformationSensor other) {
		return new MultipliedTransformationSensor(this, other);
	}

	/**
	 * Gets a {@link VectorSensor} measuring the translational component of this
	 * TransformationSensor.
	 * 
	 * @return the translation sensor
	 */
	public VectorSensor getTranslationSensor() {
		return new VectorFromTransformationSensor(this);
	}

	/**
	 * Gets a {@link RotationSensor} measuring the rotational component of this
	 * TransformationSensor.
	 * 
	 * @return the rotation sensor
	 */
	public RotationSensor getRotationSensor() {
		return new RotationFromTransformationSensor(this);
	}

	/**
	 * Gets a {@link DoubleSensor} measuring the x axis translation component of
	 * this TransformationSensor.
	 * 
	 * @return Sensor measuring the x translation
	 */
	public DoubleSensor getX() {
		return getTranslationSensor().getXSensor();
	}

	/**
	 * Gets a {@link DoubleSensor} measuring the y axis translation component of
	 * this TransformationSensor.
	 * 
	 * @return Sensor measuring the y translation
	 */
	public DoubleSensor getY() {
		return getTranslationSensor().getYSensor();
	}

	/**
	 * Gets a {@link DoubleSensor} measuring the z axis translation component of
	 * this TransformationSensor.
	 * 
	 * @return Sensor measuring the z translation
	 */
	public DoubleSensor getZ() {
		return getTranslationSensor().getZSensor();
	}

	/**
	 * Gets a {@link DoubleSensor} measuring the z axis rotation (A) component of
	 * this TransformationSensor.
	 * 
	 * @return Sensor measuring the A rotation
	 */
	public DoubleSensor getA() {
		return getRotationSensor().getASensor();
	}

	/**
	 * Gets a {@link DoubleSensor} measuring the y axis rotation (B) component of
	 * this TransformationSensor.
	 * 
	 * @return Sensor measuring the B rotation
	 */
	public DoubleSensor getB() {
		return getRotationSensor().getBSensor();
	}

	/**
	 * Gets a {@link DoubleSensor} measuring the x axis rotation (C) component of
	 * this TransformationSensor.
	 * 
	 * @return Sensor measuring the C rotation
	 */
	public DoubleSensor getC() {
		return getRotationSensor().getCSensor();
	}

	/**
	 * Creates a new {@link TransformationSensor} from a given
	 * {@link RotationSensor}, which determines the rotational part of the new
	 * sensor. The translation delivered by the newly created sensor will be a zero
	 * vector.
	 * 
	 * @param sensor the RotationSensor constituting the new TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static TransformationSensor fromRotationSensor(RotationSensor sensor) {
		return fromComponents(VectorSensor.fromConstant(Vector.getNullVector()), sensor);
	}

	/**
	 * Creates a new {@link TransformationSensor} from a given {@link VectorSensor},
	 * which determines the translational part of the new sensor. The rotation
	 * delivered by the newly created sensor will be a zero rotation.
	 * 
	 * @param sensor the translation sensor constituting the new
	 *               TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static TransformationSensor fromVectorSensor(VectorSensor sensor) {
		return fromComponents(sensor, RotationSensor.fromConstant(new Rotation()));
	}

	/**
	 * Creates a new {@link TransformationSensor} from a given constant
	 * {@link Transformation}.
	 * 
	 * @param trans the constant Transformation determining the value of the new
	 *              TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static TransformationSensor fromConstant(Transformation trans) {
		return new ConstantTransformationSensor(trans);
	}

	/**
	 * Creates a new {@link TransformationSensor} from given constant transformation
	 * values.
	 * 
	 * @param x the x axis translation value of the new TransformationSensor
	 * @param y the y axis translation value of the new TransformationSensor
	 * @param z the z axis translation value of the new TransformationSensor
	 * @param a the z axis rotation value (A) of the new TransformationSensor
	 * @param b the y axis rotation value (B) of the new TransformationSensor
	 * @param c the x axis rotation value (C) of the new TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static TransformationSensor fromConstant(double x, double y, double z, double a, double b, double c) {
		return fromConstant(new Transformation(x, y, z, a, b, c));
	}

	/**
	 * Creates a new {@link TransformationSensor} from given constant translation
	 * values. The rotation delivered by the newly created sensor will be a zero
	 * rotation.
	 * 
	 * @param x the x axis translation value of the new TransformationSensor
	 * @param y the y axis translation value of the new TransformationSensor
	 * @param z the z axis translation value of the new TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static TransformationSensor fromConstant(double x, double y, double z) {
		return fromConstant(new Transformation(x, y, z, 0, 0, 0));
	}

	/**
	 * Creates a new {@link TransformationSensor} from given {@link VectorSensor}
	 * (determining translation values} and {@link RotationSensor} (determining
	 * rotation values).
	 * 
	 * @param translationSensor the VectorSensor determining translation values
	 * @param rotationSensor    the RotationSensor determining rotation values
	 * @return the new TransformationSensor
	 */
	public static TransformationSensor fromComponents(VectorSensor translationSensor, RotationSensor rotationSensor) {
		return new TransformationFromComponentsSensor(translationSensor, rotationSensor);
	}

	/**
	 * Creates a new {@link TransformationSensor} from given {@link DoubleSensor}s,
	 * determining translation values and rotation values.
	 * 
	 * @param xSensor sensor determining the x axis translation value of the new
	 *                TransformationSensor
	 * @param ySensor sensor determining the y axis translation value of the new
	 *                TransformationSensor
	 * @param zSensor sensor determining the z axis translation value of the new
	 *                TransformationSensor
	 * @param aSensor sensor determining the z axis rotation value (A) of the new
	 *                TransformationSensor
	 * @param bSensor sensor determining the y axis rotation value (B) of the new
	 *                TransformationSensor
	 * @param cSensor sensor determining the x axis rotation value (C) of the new
	 *                TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static TransformationSensor fromComponents(DoubleSensor xSensor, DoubleSensor ySensor, DoubleSensor zSensor,
			DoubleSensor aSensor, DoubleSensor bSensor, DoubleSensor cSensor) {
		return fromComponents(VectorSensor.fromComponents(xSensor, ySensor, zSensor),
				RotationSensor.fromABC(aSensor, bSensor, cSensor));
	}

	/**
	 * Creates a new {@link JavaTransformationSensor} which allows to set all
	 * transformation values directly from the application. Those values are also
	 * propagated to running {@link Command}s.
	 * 
	 * @param runtime the {@link RoboticsRuntime} for which the new sensor is valid
	 * @return the new JavaTransformationSensor
	 */
	public static JavaTransformationSensor fromJava(RoboticsRuntime runtime) {
		return new JavaTransformationSensor(runtime);
	}

	public static TransformationSensor conditional(BooleanSensor condition, TransformationSensor ifTrue,
			TransformationSensor ifFalse) {
		return new TransformationConditionalSensor(condition, ifTrue, ifFalse);
	}

	public TransformationSensor fromHistory(DoubleSensor age, double maxAge) {
		return new TransformationAtTimeSensor(this, age, maxAge);
	}

	public TransformationSensor fromHistory(double age) {
		return new TransformationAtTimeSensor(this, DoubleSensor.fromValue(age), age);
	}

	public TransformationSensor slidingAverage(double duration) {
		return fromComponents(getTranslationSensor().slidingAverage(duration),
				getRotationSensor().slidingAverage(duration));
	}
}
