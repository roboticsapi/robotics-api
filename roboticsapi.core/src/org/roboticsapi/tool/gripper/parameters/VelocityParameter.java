/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper.parameters;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.tool.gripper.GripperParameters;
import org.roboticsapi.tool.gripper.VelocityConsideringGripper;

/**
 * Velocity parameter for grippers
 * 
 * @see VelocityConsideringGripper
 */
public class VelocityParameter implements GripperParameters {

	private final double velocity;

	/**
	 * Constructor.
	 * 
	 * @param force the parameter's velocity in [m/s].
	 */
	public VelocityParameter(double velocity) {
		super();
		this.velocity = velocity;
	}

	/**
	 * Returns the specified velocity of the gripper's fingers in [m/s].
	 * 
	 * @return the specified velocity of the gripper's fingers in [m/s].
	 */
	public double getVelocity() {
		return velocity;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof VelocityParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		VelocityParameter bound = (VelocityParameter) boundingObject;

		return getVelocity() <= bound.getVelocity();
	}

}
