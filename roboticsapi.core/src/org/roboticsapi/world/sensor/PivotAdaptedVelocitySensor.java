/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Point;

public final class PivotAdaptedVelocitySensor extends VelocitySensor {

	private final VelocitySensor other;

	public PivotAdaptedVelocitySensor(VelocitySensor other, Point pivotPoint) {
		super(selectRuntime(other), other.getMovingFrame(), other.getReferenceFrame(), pivotPoint,
				other.getOrientation());
		addInnerSensors(other);
		this.other = other;
	}

	public VelocitySensor getOtherSensor() {
		return other;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((PivotAdaptedVelocitySensor) obj).other)
				&& getPivotPoint().equals(((PivotAdaptedVelocitySensor) obj).getPivotPoint());
	}

	@Override
	public int hashCode() {
		return classHash(other, getPivotPoint());
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	public String toString() {
		return "adaptPivot(" + other + ", " + getPivotPoint() + ")";
	}
}
