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

public interface GripperVelocitySensorInterface extends SensorInterface {

	/**
	 * Returns the gripper's min. permitted velocity in [m/s].
	 *
	 * @return the min. permitted velocity in [m/s].
	 */
	double getMinimumVelocity();

	/**
	 * Returns the gripper's max. permitted velocity in [m/s].
	 *
	 * @return the max. permitted velocity in [m/s].
	 */
	double getMaximumVelocity();

	/**
	 * Returns a sensor for the gripper's velocity.
	 *
	 * @return a sensor for velocity. It might be <code>null</code> if sensor is not
	 *         available.
	 */
	public RealtimeDouble getVelocity();

	/**
	 * Returns the gripper's current velocity in [m/s].
	 *
	 * @return the current velocity in [m/s].
	 * @throws RealtimeValueReadException if a force sensor is not available or the
	 *                                    current value cannot be retrieved.
	 */
	public default double getCurrentVelocity() throws RealtimeValueReadException {
		return getVelocity().getCurrentValue();
	}

}
