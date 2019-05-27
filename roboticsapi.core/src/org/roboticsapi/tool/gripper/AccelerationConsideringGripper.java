/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.tool.gripper.parameters.AccelerationParameter;

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
	double getMinimumAcceleration();

	/**
	 * Returns the gripper's max. permitted acceleration in [m/s^2].
	 * 
	 * @return the max. permitted acceleration in [m/s^2].
	 */
	double getMaximumAcceleration();

	/**
	 * Returns the gripper's current acceleration in [m/s^2].
	 * 
	 * @return the current acceleration in [m/s^2].
	 * @throws SensorReadException if an acceleration sensor is not available or the
	 *                             current value cannot be retrieved.
	 */
	double getCurrentAcceleration() throws SensorReadException;

	/**
	 * Returns a sensor for the gripper's acceleration.
	 * 
	 * @return a sensor for acceleration. It might be <code>null</code> if sensor is
	 *         not available.
	 */
	DoubleSensor getAccelerationSensor();

}
