/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class AxisAngleToRealtimeRotation extends RealtimeRotation {

	private final RealtimeVector axis;
	private final RealtimeDouble angle;

	AxisAngleToRealtimeRotation(RealtimeVector axis, RealtimeDouble angle) {
		super(axis, angle);
		this.axis = axis;
		this.angle = angle;
	}

	@Override
	public RealtimeVector getAxis() {
		return axis;
	}

	@Override
	public RealtimeDouble getAngle() {
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
		return classEqual(obj) && axis.equals(((AxisAngleToRealtimeRotation) obj).axis)
				&& angle.equals(((AxisAngleToRealtimeRotation) obj).angle);
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
