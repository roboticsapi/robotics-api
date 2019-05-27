/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

public final class TransVelVectorFromVelocitySensor extends VectorSensor {

	private final VelocitySensor velocitySensor;

	public TransVelVectorFromVelocitySensor(VelocitySensor velocitySensor) {
		super(selectRuntime(velocitySensor));
		addInnerSensors(velocitySensor);
		this.velocitySensor = velocitySensor;
	}

	public VelocitySensor getVelocitySensor() {
		return velocitySensor;
	}

	@Override
	protected Vector calculateCheapValue() {
		Twist cheapValue = getVelocitySensor().getCheapValue();
		return cheapValue == null ? null : cheapValue.getTransVel();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && velocitySensor.equals(((TransVelVectorFromVelocitySensor) obj).velocitySensor);
	}

	@Override
	public int hashCode() {
		return classHash(velocitySensor);
	}

	@Override
	public boolean isAvailable() {
		return velocitySensor.isAvailable();
	}

	@Override
	public String toString() {
		return "transVel(" + velocitySensor + ")";
	}
}
