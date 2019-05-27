/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;

public final class RotationFromAxisAngleSensor extends RotationSensor {

	private final VectorSensor axis;
	private final DoubleSensor angle;

	public RotationFromAxisAngleSensor(VectorSensor axis, DoubleSensor angle) {
		super(selectRuntime(axis, angle));
		addInnerSensors(axis, angle);
		this.axis = axis;
		this.angle = angle;
	}

	public VectorSensor getAxis() {
		return axis;
	}

	public DoubleSensor getAngle() {
		return angle;
	}

	@Override
	protected Rotation calculateCheapValue() {
		Vector axis = getAxis().getCheapValue();
		Double angle = getAngle().getCheapValue();
		return (axis == null || angle == null) ? null : new Rotation(axis, angle);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && axis.equals(((RotationFromAxisAngleSensor) obj).axis)
				&& angle.equals(((RotationFromAxisAngleSensor) obj).angle);
	}

	@Override
	public int hashCode() {
		return classHash(axis, angle);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(axis, angle);
	}

	@Override
	public String toString() {
		return "rotation(Axis:" + axis + ", Angle:" + angle + ")";
	}

}
