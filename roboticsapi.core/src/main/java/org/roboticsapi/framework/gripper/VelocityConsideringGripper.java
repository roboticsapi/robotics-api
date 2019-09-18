/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.framework.gripper.parameters.VelocityParameter;

/**
 * This interface represents a gripper with velocity skills.
 * <p>
 * This interface can be implemented by any gripper which can cope with velocity
 * data - in particular as device parameter for gripping activities (cf.
 * {@link VelocityParameter}).
 *
 * @see VelocityParameter
 */
public interface VelocityConsideringGripper extends Gripper {

	/**
	 * Returns the gripper's min. permitted velocity in [m/s].
	 *
	 * @return the min. permitted velocity in [m/s].
	 */
	public double getMinimumVelocity();

	/**
	 * Returns the gripper's max. permitted velocity in [m/s].
	 *
	 * @return the max. permitted velocity in [m/s].
	 */
	public double getMaximumVelocity();

}
