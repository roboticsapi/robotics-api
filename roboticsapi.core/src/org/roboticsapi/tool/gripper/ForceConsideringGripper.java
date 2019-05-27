/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.tool.gripper.parameters.ForceParameter;

/**
 * This interface represents a gripper with force skills.
 * <p>
 * This interface can be implemented by any gripper which can cope with force
 * data - in particular as device parameter for gripping activities (cf.
 * {@link ForceParameter}).
 * 
 * @see ForceParameter
 */
public interface ForceConsideringGripper extends Gripper {

	/**
	 * Returns the gripper's min. permitted force in [N].
	 * 
	 * @return the min. permitted force in [N].
	 */
	double getMinimumForce();

	/**
	 * Returns the gripper's max. permitted force in [N].
	 * 
	 * @return the max. permitted force in [N].
	 */
	double getMaximumForce();

	/**
	 * Returns the gripper's current force in [N].
	 * 
	 * @return the current force in [N].
	 * @throws SensorReadException if a force sensor is not available or the current
	 *                             value cannot be retrieved.
	 */
	double getCurrentForce() throws SensorReadException;

	/**
	 * Returns a sensor for the gripper's force in [N].
	 * 
	 * @return a sensor for force. It might be <code>null</code> if sensor is not
	 *         available.
	 */
	DoubleSensor getForceSensor();

}
