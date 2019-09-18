/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper.activity;

import org.roboticsapi.core.SensorInterface;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public interface GripperAccelerationSensorInterface extends SensorInterface {

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

	/**
	 * Returns the gripper's current acceleration in [m/s^2].
	 *
	 * @return the current acceleration in [m/s^2].
	 * @throws RealtimeValueReadException if an acceleration sensor is not available
	 *                                    or the current value cannot be retrieved.
	 */
	public default double getCurrentAcceleration() throws RealtimeValueReadException {
		return getAcceleration().getCurrentValue();
	}

	/**
	 * Returns a sensor for the gripper's acceleration.
	 *
	 * @return a sensor for acceleration. It might be <code>null</code> if sensor is
	 *         not available.
	 */
	public RealtimeDouble getAcceleration();
}
