/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import java.util.Map;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * A Robotics API {@link RealtimeValue} measuring {@link Transformation} values.
 */
public abstract class RealtimeTransformation extends RealtimeValue<Transformation> {
	public static final RealtimeTransformation IDENTITY = new ConstantRealtimeTransformation(Transformation.IDENTITY);

	@Override
	public final RealtimeTransformation substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeTransformation) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeTransformation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	/**
	 * Creates a new TransformationSensor that is valid for given
	 * {@link RealtimeValues}.
	 *
	 * @param values the values this value is based on
	 */
	public RealtimeTransformation(RealtimeValue<?>... values) {
		super(values);
	}

	/**
	 * Creates a new TransformationSensor that is valid for a given
	 * {@link RoboticsRuntime}.
	 *
	 * @param runtime the runtime
	 */
	public RealtimeTransformation(RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Creates a new {@link RealtimeTransformation} which delivers the inverted
	 * {@link Transformation} values that are measured by this TransformationSensor.
	 *
	 * @return the inverted TransformationSensor
	 */
	public RealtimeTransformation invert() {
		return new InvertedRealtimeTransformation(this);
	}

	/**
	 * Creates a new {@link RealtimeTransformation} which delivers the values of
	 * this TransformationSensor, multiplied with the values delivered by a given
	 * TransformationSensor.
	 *
	 * @param other the other TransformationSensor, where this
	 *              TransformationSensor's values are multiplied with the values of
	 *              the other TransformationSensor
	 * @return TransformationSensor delivering the result values of the
	 *         multiplication
	 */
	public RealtimeTransformation multiply(RealtimeTransformation other) {
		if (CONSTANT_FOLDING && other.isConstant() && other.getCheapValue().isIdentityTransformation()) {
			return this;
		} else {
			return new MultipliedRealtimeTransformation(this, other);
		}
	}

	public RealtimeTransformation changeOrientation(RealtimeRotation orientationChange) {
		if (CONSTANT_FOLDING && orientationChange.isConstant()
				&& orientationChange.getCheapValue().isIdentityRotation()) {
			return this;
		} else {
			return new OrientationAdaptedRealtimeTransformation(this, orientationChange);
		}
	}

	public RealtimeTwist derive() {
		if (isConstant()) {
			return RealtimeTwist.createFromConstant(new Twist());
		} else {
			return new RealtimeTransformationToRealtimeTwist(this);
		}
	}

	/**
	 * Gets a {@link RealtimeVector} measuring the translational component of this
	 * RealtimeTransformation.
	 *
	 * @return the translation
	 */
	public RealtimeVector getTranslation() {
		return new GetTranslationFromTransformationRealtimeVector(this);
	}

	/**
	 * Gets a {@link RealtimeRotation} measuring the rotational component of this
	 * RealtimeTransformation.
	 *
	 * @return the rotation
	 */
	public RealtimeRotation getRotation() {
		return new TransformationToRealtimeRotation(this);
	}

	/**
	 * Gets a {@link RealtimeDouble} measuring the x axis translation component of
	 * this TransformationSensor.
	 *
	 * @return Sensor measuring the x translation
	 */
	public RealtimeDouble getX() {
		return getTranslation().getX();
	}

	/**
	 * Gets a {@link RealtimeDouble} measuring the y axis translation component of
	 * this TransformationSensor.
	 *
	 * @return Sensor measuring the y translation
	 */
	public RealtimeDouble getY() {
		return getTranslation().getY();
	}

	/**
	 * Gets a {@link RealtimeDouble} measuring the z axis translation component of
	 * this TransformationSensor.
	 *
	 * @return Sensor measuring the z translation
	 */
	public RealtimeDouble getZ() {
		return getTranslation().getZ();
	}

	/**
	 * Gets a {@link RealtimeDouble} measuring the z axis rotation (A) component of
	 * this TransformationSensor.
	 *
	 * @return Sensor measuring the A rotation
	 */
	public RealtimeDouble getA() {
		return getRotation().getA();
	}

	/**
	 * Gets a {@link RealtimeDouble} measuring the y axis rotation (B) component of
	 * this TransformationSensor.
	 *
	 * @return Sensor measuring the B rotation
	 */
	public RealtimeDouble getB() {
		return getRotation().getB();
	}

	/**
	 * Gets a {@link RealtimeDouble} measuring the x axis rotation (C) component of
	 * this TransformationSensor.
	 *
	 * @return Sensor measuring the C rotation
	 */
	public RealtimeDouble getC() {
		return getRotation().getC();
	}

	/**
	 * Creates a new {@link RealtimeTransformation} from a given
	 * {@link RealtimeRotation}, which determines the rotational part of the new
	 * sensor. The translation delivered by the newly created sensor will be a zero
	 * vector.
	 *
	 * @param rotation the RotationSensor constituting the new TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static RealtimeTransformation createFromRotation(RealtimeRotation rotation) {
		return createFromVectorRotation(RealtimeVector.createFromConstant(Vector.getNullVector()), rotation);
	}

	/**
	 * Creates a new {@link RealtimeTransformation} from a given
	 * {@link RealtimeVector}, which determines the translational part of the new
	 * sensor. The rotation delivered by the newly created sensor will be a zero
	 * rotation.
	 *
	 * @param vector the translation sensor constituting the new
	 *               TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static RealtimeTransformation createFromVector(RealtimeVector vector) {
		return createFromVectorRotation(vector, RealtimeRotation.IDENTITY);
	}

	/**
	 * Creates a new {@link RealtimeTransformation} from a given constant
	 * {@link Transformation}.
	 *
	 * @param trans the constant Transformation determining the value of the new
	 *              TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static RealtimeTransformation createfromConstant(Transformation trans) {
		return new ConstantRealtimeTransformation(trans);
	}

	/**
	 * Creates a new {@link RealtimeTransformation} from given constant
	 * transformation values.
	 *
	 * @param x the x axis translation value of the new TransformationSensor
	 * @param y the y axis translation value of the new TransformationSensor
	 * @param z the z axis translation value of the new TransformationSensor
	 * @param a the z axis rotation value (A) of the new TransformationSensor
	 * @param b the y axis rotation value (B) of the new TransformationSensor
	 * @param c the x axis rotation value (C) of the new TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static RealtimeTransformation createFromConstantXYZABC(double x, double y, double z, double a, double b,
			double c) {
		return createfromConstant(new Transformation(x, y, z, a, b, c));
	}

	/**
	 * Creates a new {@link RealtimeTransformation} from given constant translation
	 * values. The rotation delivered by the newly created sensor will be a zero
	 * rotation.
	 *
	 * @param x the x axis translation value of the new TransformationSensor
	 * @param y the y axis translation value of the new TransformationSensor
	 * @param z the z axis translation value of the new TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static RealtimeTransformation createFromConstantXYZ(double x, double y, double z) {
		return createfromConstant(new Transformation(x, y, z, 0, 0, 0));
	}

	/**
	 * Creates a new {@link RealtimeTransformation} from given
	 * {@link RealtimeVector} (determining translation values} and
	 * {@link RealtimeRotation} (determining rotation values).
	 *
	 * @param translation the VectorSensor determining translation values
	 * @param rotation    the RotationSensor determining rotation values
	 * @return the new TransformationSensor
	 */
	public static RealtimeTransformation createFromVectorRotation(RealtimeVector translation,
			RealtimeRotation rotation) {
		if (CONSTANT_FOLDING && translation.isConstant() && rotation.isConstant()) {
			return createfromConstant(new Transformation(rotation.getCheapValue(), translation.getCheapValue()));
		} else {
			return new XYZABCToRealtimeTransformation(translation, rotation);
		}
	}

	/**
	 * Creates a new {@link RealtimeTransformation} from given
	 * {@link RealtimeDouble}s, determining translation values and rotation values.
	 *
	 * @param x sensor determining the x axis translation value of the new
	 *          TransformationSensor
	 * @param y sensor determining the y axis translation value of the new
	 *          TransformationSensor
	 * @param z sensor determining the z axis translation value of the new
	 *          TransformationSensor
	 * @param a sensor determining the z axis rotation value (A) of the new
	 *          TransformationSensor
	 * @param b sensor determining the y axis rotation value (B) of the new
	 *          TransformationSensor
	 * @param c sensor determining the x axis rotation value (C) of the new
	 *          TransformationSensor
	 * @return the new TransformationSensor
	 */
	public static RealtimeTransformation createFromXYZABC(RealtimeDouble x, RealtimeDouble y, RealtimeDouble z,
			RealtimeDouble a, RealtimeDouble b, RealtimeDouble c) {
		return createFromVectorRotation(RealtimeVector.createFromXYZ(x, y, z), RealtimeRotation.createFromABC(a, b, c));
	}

	/**
	 * Creates a new {@link WritableRealtimeTransformation} which allows to set all
	 * transformation values directly from the application. Those values are also
	 * propagated to running {@link Command}s.
	 *
	 * @param defaultValue the default value
	 * @return the new WritableRealtimeTransformation
	 */
	public static WritableRealtimeTransformation createWritable(Transformation defaultValue) {
		return new WritableRealtimeTransformation(defaultValue);
	}

	public static RealtimeTransformation createConditional(RealtimeBoolean condition, RealtimeTransformation ifTrue,
			RealtimeTransformation ifFalse) {
		if (CONSTANT_FOLDING && condition.isConstant()) {
			return condition.getCheapValue() ? ifTrue : ifFalse;
		} else {
			return new ConditionalRealtimeTransformation(condition, ifTrue, ifFalse);
		}
	}

	@Override
	public RealtimeTransformation fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeTransformationAtTime(this, age, maxAge);
	}

	@Override
	public RealtimeTransformation fromHistory(double age) {
		return new RealtimeTransformationAtTime(this, RealtimeDouble.createFromConstant(age), age);
	}

	public RealtimeTransformation slidingAverage(double duration) {
		return createFromVectorRotation(getTranslation().slidingAverage(duration),
				getRotation().slidingAverage(duration));
	}

	public RealtimePose asPose(Frame reference) {
		return RealtimePose.createFromTransformation(reference, this);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeTransformationIsNull(this);
	}

}
