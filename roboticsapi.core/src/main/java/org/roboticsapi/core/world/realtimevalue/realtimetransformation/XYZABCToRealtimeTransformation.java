/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * The TransformationFromComponentsSensor constructs a TransformationSensor out
 * of singular values representing the TransformationSensor's components.
 */
public class XYZABCToRealtimeTransformation extends RealtimeTransformation {

	@Override
	protected RealtimeTransformation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new XYZABCToRealtimeTransformation(translation.substitute(substitutionMap),
				rotation.substitute(substitutionMap));
	}

	private final RealtimeVector translation;
	private final RealtimeRotation rotation;

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
	XYZABCToRealtimeTransformation(RealtimeDouble x, RealtimeDouble y, RealtimeDouble z, RealtimeDouble a,
			RealtimeDouble b, RealtimeDouble c) {
		this(RealtimeVector.createFromXYZ(x, y, z), RealtimeRotation.createFromABC(a, b, c));
	}

	XYZABCToRealtimeTransformation(RealtimeVector translation, RealtimeRotation rotation) {
		super(translation, rotation);
		this.translation = translation;
		this.rotation = rotation;
	}

	@Override
	protected Transformation calculateCheapValue() {
		Vector trans = translation.getCheapValue();
		Rotation rot = rotation.getCheapValue();

		return (trans == null || rot == null) ? null : new Transformation(rot, trans);
	}

	@Override
	public RealtimeVector getTranslation() {
		return translation;
	}

	@Override
	public RealtimeRotation getRotation() {
		return rotation;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && rotation.equals(((XYZABCToRealtimeTransformation) obj).rotation)
				&& translation.equals(((XYZABCToRealtimeTransformation) obj).translation);
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
