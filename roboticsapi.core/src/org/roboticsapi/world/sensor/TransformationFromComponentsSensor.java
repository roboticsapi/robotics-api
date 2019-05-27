/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;

/**
 * The TransformationFromComponentsSensor constructs a TransformationSensor out
 * of singular values representing the TransformationSensor's components.
 */
public class TransformationFromComponentsSensor extends TransformationSensor {

	private final VectorSensor translation;
	private final RotationSensor rotation;

	/**
	 * Instantiates a new TransformationFromComponentsSensor.
	 *
	 * @param from the 'from' end of the Transformation
	 * @param to   the 'to' end of the Transformation
	 * @param x    the Sensor delivering the Transformation's X value
	 * @param y    the Sensor delivering the Transformation's Y value
	 * @param z    the Sensor delivering the Transformation's Z value
	 * @param a    the Sensor delivering the Transformation's A value
	 * @param b    the Sensor delivering the Transformation's B value
	 * @param c    the Sensor delivering the Transformation's C value
	 * @throws RoboticsException if the Sensors are from different RoboticsRuntimes
	 */
	public TransformationFromComponentsSensor(DoubleSensor x, DoubleSensor y, DoubleSensor z, DoubleSensor a,
			DoubleSensor b, DoubleSensor c) {
		this(VectorSensor.fromComponents(x, y, z), RotationSensor.fromABC(a, b, c));
	}

	public TransformationFromComponentsSensor(VectorSensor translationSensor, RotationSensor rotationSensor) {
		super(selectRuntime(translationSensor, rotationSensor));
		addInnerSensors(translationSensor, rotationSensor);
		translation = translationSensor;
		rotation = rotationSensor;
	}

	@Override
	protected Transformation calculateCheapValue() {
		Vector trans = translation.getCheapValue();
		Rotation rot = rotation.getCheapValue();

		return (trans == null || rot == null) ? null : new Transformation(rot, trans);
	}

	@Override
	public VectorSensor getTranslationSensor() {
		return translation;
	}

	@Override
	public RotationSensor getRotationSensor() {
		return rotation;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && rotation.equals(((TransformationFromComponentsSensor) obj).rotation)
				&& translation.equals(((TransformationFromComponentsSensor) obj).translation);
	}

	@Override
	public int hashCode() {
		return classHash(rotation, translation);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(rotation, translation);
	}

	@Override
	public String toString() {
		return "transformation(" + translation + ", " + rotation + ")";
	}
}
