/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;

/**
 * A Robotics API RelationSensor is a {@link Sensor} based on a
 * {@link TransformationSensor}. It measures the {@link Transformation} between
 * a "from" and a "to" {@link Frame}.
 */
public final class RelationSensor extends TransformationSensor {
	private final Frame from;
	private final Frame to;
	private final TransformationSensor transformationSensor;

	/**
	 * Creates a new RelationSensor based on a given {@link TransformationSensor}
	 * which measures the Transformation between two given Frames.
	 *
	 * @param transformationSensor the TransformationSensor that the created
	 *                             RelationSensor is based on
	 * @param from                 the Frame based on which the Transformation is
	 *                             measured
	 * @param to                   the Frame to which the Transformation is measured
	 */
	public RelationSensor(TransformationSensor transformationSensor, Frame from, Frame to) {
		super(selectRuntime(transformationSensor));
		addInnerSensors(transformationSensor);
		this.transformationSensor = transformationSensor;
		this.from = from;
		this.to = to;
	}

	/**
	 * Gets the {@link Frame} based on which this sensor measures the
	 * Transformation.
	 *
	 * @return the "from" Frame
	 */
	public Frame getFrom() {
		return from;
	}

	/**
	 * Gets the {@link Frame} to which this sensor measures the Transformation.
	 *
	 * @return the "to" Frame
	 */
	public Frame getTo() {
		return to;
	}

	@Override
	protected Transformation calculateCheapValue() {
		return getTransformationSensor().getCheapValue();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.roboticsapi.core.Sensor#addListener(org.roboticsapi.core.SensorListener )
	 */
	@Override
	public synchronized void addListener(final SensorListener<Transformation> listener) throws RoboticsException {
		getTransformationSensor().addListener(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Sensor#removeListener(org.roboticsapi.core.
	 * SensorListener)
	 */
	@Override
	public synchronized void removeListener(SensorListener<Transformation> listener) throws RoboticsException {
		getTransformationSensor().removeListener(listener);
	}

	/**
	 * Reinterprets this RelationSensor in the context of two given Frames, i.e.
	 * creates a new RelationSensor based on the same {@link TransformationSensor},
	 * but interprets the measured Transformation values in a different context. The
	 * Transformation values itself are not changed by the new sensor.
	 *
	 * @param from the Frame based on which the new sensor measures Transformation
	 *             values
	 * @param to   the Frame to which the new sensor measures Transformation values
	 * @return the new RelationSensor
	 */
	public RelationSensor reinterpret(Frame from, Frame to) {
		return new RelationSensor(getTransformationSensor(), from, to);
	}

	/**
	 * Creates a new RelationSensor which inverts this RelationSensor. This is done
	 * by inverting the underlying {@link TransformationSensor} and by flipping this
	 * sensor's "from" and "to" Frames.
	 *
	 * @return the new RelationSensor
	 */
	@Override
	public RelationSensor invert() {
		return new RelationSensor(getTransformationSensor().invert(), getTo(), getFrom());
	}

	/**
	 * Creates a new RelationSensor which represents the relation of this sensor
	 * multiplied with the relation of a given other RelationSensor. The underlying
	 * {@link TransformationSensor}s are multiplied and the new RelationSensor
	 * measures the relation between this RelationSensor's "from" Frame and the
	 * given RelationSensor's "to" Frame.
	 *
	 * @param other the RelationSensor to multiply this RelationSensor with
	 * @return the new RelationSensor
	 */
	public RelationSensor multiply(RelationSensor other) {
		return new RelationSensor(getTransformationSensor().multiply(other.getTransformationSensor()), getFrom(),
				other.getTo());
	}

	public DirectionSensor getDirectionSensor() {
		return new DirectionSensor(getTransformationSensor().getTranslationSensor(), getFrom().getOrientation());
	}

	public OrientationSensor getOrientationSensor() {
		return new OrientationSensor(getTransformationSensor().getRotationSensor(), getFrom());
	}

	@Override
	public DoubleSensor getX() {
		return getTransformationSensor().getX();
	}

	@Override
	public DoubleSensor getY() {
		return getTransformationSensor().getY();
	}

	@Override
	public DoubleSensor getZ() {
		return getTransformationSensor().getZ();
	}

	@Override
	public DoubleSensor getA() {
		return getTransformationSensor().getA();
	}

	@Override
	public DoubleSensor getB() {
		return getTransformationSensor().getB();
	}

	@Override
	public DoubleSensor getC() {
		return getTransformationSensor().getC();
	}

	public static RelationSensor fromConstant(Frame from, Frame to, Transformation trans) {
		return new RelationSensor(TransformationSensor.fromConstant(trans), from, to);
	}

	public TransformationSensor getTransformationSensor() {
		return transformationSensor;
	}

	@Override
	public RelationSensor fromHistory(DoubleSensor age, double maxAge) {
		return new RelationSensor(getTransformationSensor().fromHistory(age, maxAge), getFrom(), getTo());
	}

	@Override
	public RelationSensor fromHistory(double age) {
		return new RelationSensor(getTransformationSensor().fromHistory(age), getFrom(), getTo());
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && from.equals(((RelationSensor) obj).from) && to.equals(((RelationSensor) obj).to)
				&& transformationSensor.equals(((RelationSensor) obj).transformationSensor);
	}

	@Override
	public int hashCode() {
		return classHash(from, to, transformationSensor);
	}

	@Override
	public boolean isAvailable() {
		return transformationSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "relation(" + from + ", " + to + ", " + transformationSensor + ")";
	}
}
