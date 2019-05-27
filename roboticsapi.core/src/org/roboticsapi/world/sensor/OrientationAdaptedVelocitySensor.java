/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Orientation;

public final class OrientationAdaptedVelocitySensor extends VelocitySensor {

	private final Orientation newOrientation;
	private final VelocitySensor other;

	public OrientationAdaptedVelocitySensor(VelocitySensor other, Orientation orientation) {
		super(selectRuntime(other), other.getMovingFrame(), other.getReferenceFrame(), other.getPivotPoint(),
				orientation);
		addInnerSensors(other);
		this.other = other;
		newOrientation = orientation;
	}

	public Orientation getNewOrientation() {
		return newOrientation;
	}

	public VelocitySensor getOtherSensor() {
		return other;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((OrientationAdaptedVelocitySensor) obj).other)
				&& newOrientation.equals(((OrientationAdaptedVelocitySensor) obj).newOrientation);
	}

	@Override
	public int hashCode() {
		return classHash(other, newOrientation);
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	public String toString() {
		return "adaptOrientation(" + other + ", " + newOrientation + ")";
	}
}
