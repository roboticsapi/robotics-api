/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper.parameters;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.tool.gripper.ForceConsideringGripper;
import org.roboticsapi.tool.gripper.GripperParameters;

/**
 * Force parameter for grippers
 * 
 * @see ForceConsideringGripper
 */
public class ForceParameter implements GripperParameters {

	private final double force;

	/**
	 * Constructor.
	 * 
	 * @param force the parameter's force in [N].
	 */
	public ForceParameter(double force) {
		super();
		this.force = force;
	}

	/**
	 * Returns the specified gripping force in [N].
	 * 
	 * @return the specified gripping force in [N]
	 */
	public double getForce() {
		return force;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof ForceParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		ForceParameter bound = (ForceParameter) boundingObject;

		return getForce() <= bound.getForce();
	}

}
