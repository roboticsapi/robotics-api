/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.framework.gripper.parameters.AccelerationParameter;

/**
 * This interface represents a gripper with acceleration skills.
 * <p>
 * This interface can be implemented by any gripper which can cope with
 * acceleration data - in particular as device parameter for gripping activities
 * (cf. {@link AccelerationParameter}).
 *
 * @see AccelerationParameter
 */
public interface AccelerationConsideringGripper extends Gripper {

	/**
	 * Returns the gripper's min. permitted acceleration in [m/s^2].
	 *
	 * @return the min. permitted acceleration in [m/s^2].
	 */
	public double getMinimumAcceleration();

	/**
	 * Returns the gripper's max. permitted acceleration in [m/s^2].
	 *
	 * @return the max. permitted acceleration in [m/s^2].
	 */
	public double getMaximumAcceleration();

}
