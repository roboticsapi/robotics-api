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

public final class ReinterpretedVelocitySensor extends VelocitySensor {

	private final VelocitySensor otherSensor;

	public ReinterpretedVelocitySensor(Frame movingFrame, Frame referenceFrame, Point pivotPoint,
			Orientation orientation, VelocitySensor otherSensor) {
		super(selectRuntime(otherSensor), movingFrame, referenceFrame, pivotPoint, orientation);
		addInnerSensors(otherSensor);
		this.otherSensor = otherSensor;
	}

	@Override
	protected Twist calculateCheapValue() {
		return getOtherSensor().getCheapValue();
	}

	public VelocitySensor getOtherSensor() {
		return otherSensor;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && otherSensor.equals(((ReinterpretedVelocitySensor) obj).otherSensor)
				&& getMovingFrame().equals(((ReinterpretedVelocitySensor) obj).getMovingFrame())
				&& getReferenceFrame().equals(((ReinterpretedVelocitySensor) obj).getReferenceFrame())
				&& getPivotPoint().equals(((ReinterpretedVelocitySensor) obj).getPivotPoint())
				&& getOrientation().equals(((ReinterpretedVelocitySensor) obj).getOrientation());
	}

	@Override
	public int hashCode() {
		return classHash(otherSensor, getMovingFrame(), getReferenceFrame(), getPivotPoint(), getOrientation());
	}

	@Override
	public boolean isAvailable() {
		return otherSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "reinterpret(" + otherSensor + ", " + getMovingFrame() + ", " + getReferenceFrame() + ", "
				+ getPivotPoint() + ", " + getOrientation() + ")";
	}

}
