/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.activity;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;

public interface JointStopInterface extends ActuatorInterface {

	/**
	 * Stop the device (bring the joint velocities to zero) in order to continue
	 * from a well-defined state. This {@link Activity} does not provide guarantees
	 * about the time waited after stopping (real-time guarantees), because the
	 * resulting position is reported to the application after the robot has
	 * stopped, and thus no further pre-planning is possible.
	 *
	 * @param parameters optional DeviceParameters
	 * @return (Non-realtime) Activity that brings the {@link Actuator} to a halt
	 *         and looks where it ended
	 * @throws RoboticsException
	 */
	public Activity stopAndLook(final DeviceParameters... parameters) throws RoboticsException;

}
