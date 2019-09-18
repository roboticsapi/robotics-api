/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper.activity;

import org.roboticsapi.core.SensorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.gripper.BaseJaw;
import org.roboticsapi.framework.gripper.Finger;

public interface GripperOpeningWidthSensorInterface extends SensorInterface {

	/**
	 * Retrieves a {@link RealtimeValue} measuring the opening width (between the
	 * {@link Finger}s) in [m].
	 *
	 * @return a {@link RealtimeValue} measuring distance between the gripping fingers or
	 *         <code>null</code> if not available.
	 */
	RealtimeDouble getOpeningWidth();

	/**
	 * Retrieves a {@link RealtimeValue} measuring the opening width (between the
	 * {@link BaseJaw}s) in [m].
	 *
	 * @return a {@link RealtimeValue} measuring distance between the base jaws or
	 *         <code>null</code> if not available.
	 */
	RealtimeDouble getBaseJawOpeningWidth();

	/**
	 * Returns the current opening width (between the {@link Finger}s) in [m].
	 *
	 * @return The current opening width between the gripping fingers.
	 *
	 * @throws RoboticsException if no position sensor is available or the position
	 *                           sensor cannot be read.
	 */
	default double getCurrentOpeningWidth() throws RoboticsException {
		RealtimeDouble distanceSensor = getOpeningWidth();

		if (distanceSensor == null) {
			throw new RoboticsException("No sensor available for measuring the finger distance.");
		}
		return distanceSensor.getCurrentValue();
	}

	/**
	 * Returns the current opening width (between the {@link BaseJaw}s) in [m].
	 *
	 * @return The current opening width between the base jaws.
	 *
	 * @throws RoboticsException if no position sensor is available or the position
	 *                           sensor cannot be read.
	 */
	default double getCurrentBaseJawOpeningWidth() throws RoboticsException {
		RealtimeDouble distanceSensor = getBaseJawOpeningWidth();

		if (distanceSensor == null) {
			throw new RoboticsException("No sensor available for measuring the base jaw distance.");
		}
		return distanceSensor.getCurrentValue();
	}

}
