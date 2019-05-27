/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Twist;

/**
 * A velocity sensor for a constant velocity
 */
public final class ConstantVelocitySensor extends VelocitySensor {

	private final Twist constantValue;

	public ConstantVelocitySensor(Frame movingFrame, Frame referenceFrame, Point pivotPoint, Orientation orientation,
			Twist constantValue) {
		super(null, movingFrame, referenceFrame, pivotPoint, orientation);
		this.constantValue = constantValue;
	}

	public Twist getConstantValue() {
		return constantValue;
	}

	@Override
	protected Twist calculateCheapValue() {
		return constantValue;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && constantValue.equals(((ConstantVelocitySensor) obj).constantValue);
	}

	@Override
	public int hashCode() {
		return classHash(constantValue);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return constantValue.toString();
	}
}
