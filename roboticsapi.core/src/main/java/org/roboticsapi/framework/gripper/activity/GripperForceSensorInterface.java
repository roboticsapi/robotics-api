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

public interface GripperForceSensorInterface extends SensorInterface {

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
	 * Returns a sensor for the gripper's force in [N].
	 *
	 * @return a sensor for force. It might be <code>null</code> if sensor is not
	 *         available.
	 */
	RealtimeDouble getForce();

	/**
	 * Returns the gripper's current force in [N].
	 *
	 * @return the current force in [N].
	 * @throws RealtimeValueReadException if a force sensor is not available or the
	 *                                    current value cannot be retrieved.
	 */
	public default double getCurrentForce() throws RealtimeValueReadException {
		return getForce().getCurrentValue();
	}

}
