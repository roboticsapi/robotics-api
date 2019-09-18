/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper.parameters;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.framework.gripper.AccelerationConsideringGripper;
import org.roboticsapi.framework.gripper.GripperParameters;

/**
 * Acceleration parameter for grippers.
 * 
 * @see AccelerationConsideringGripper
 */
public class AccelerationParameter implements GripperParameters {

	private final double acceleration;

	/**
	 * Constructor.
	 * 
	 * @param acceleration the parameter's acceleration in [m/s^2].
	 */
	public AccelerationParameter(double acceleration) {
		this.acceleration = acceleration;
	}

	/**
	 * Gets the specified acceleration in [m/s^2].
	 * 
	 * @return the specified acceleration in [m/s^2].
	 */
	public double getAcceleration() {
		return this.acceleration;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof AccelerationParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		AccelerationParameter bound = (AccelerationParameter) boundingObject;

		return getAcceleration() <= bound.getAcceleration();
	}

}
