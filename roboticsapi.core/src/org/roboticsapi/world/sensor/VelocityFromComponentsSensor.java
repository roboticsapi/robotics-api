/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Twist;

/**
 * Combines a velocity sensor from translational/rotational motion directions
 */
public final class VelocityFromComponentsSensor extends VelocitySensor {

	private final DirectionSensor translationVelocitySensor;
	private final DirectionSensor rotationVelocitySensor;

	public VelocityFromComponentsSensor(DirectionSensor translationVelocitySensor,
			DirectionSensor rotationVelocitySensor, Frame movingFrame, Frame referenceFrame, Point pivotPoint) {
		super(selectRuntime(translationVelocitySensor, rotationVelocitySensor), movingFrame, referenceFrame, pivotPoint,
				rotationVelocitySensor.getOrientation());
		addInnerSensors(translationVelocitySensor, rotationVelocitySensor);
		if (!translationVelocitySensor.getOrientation().isEqualOrientation(rotationVelocitySensor.getOrientation())) {
			throw new IllegalArgumentException("Orientations of both DirectionSensors must be the same");
		}

		this.translationVelocitySensor = translationVelocitySensor.reinterpret(getOrientation());
		this.rotationVelocitySensor = rotationVelocitySensor;
	}

	@Override
	public DirectionSensor getTranslationVelocitySensor() {
		return translationVelocitySensor;
	}

	@Override
	public DirectionSensor getRotationVelocitySensor() {
		return rotationVelocitySensor;
	}

	@Override
	protected Twist calculateCheapValue() {
		Direction currentTVel = getTranslationVelocitySensor().getCheapValue();
		Direction currRVel = getRotationVelocitySensor().getCheapValue();

		return (currentTVel == null || currRVel == null) ? null
				: new Twist(currentTVel.getValue(), currRVel.getValue());
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj)
				&& rotationVelocitySensor.equals(((VelocityFromComponentsSensor) obj).rotationVelocitySensor)
				&& translationVelocitySensor.equals(((VelocityFromComponentsSensor) obj).translationVelocitySensor);
	}

	@Override
	public int hashCode() {
		return classHash(rotationVelocitySensor, translationVelocitySensor);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(rotationVelocitySensor, translationVelocitySensor);
	}

	@Override
	public String toString() {
		return "velocity(" + translationVelocitySensor + ", " + rotationVelocitySensor + ")";
	}
}
