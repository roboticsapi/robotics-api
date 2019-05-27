/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * The JointJoggingInterface provides Activities for velocity-based joint motion
 * control.
 */
public interface JointJoggingInterface extends DeviceInterface {

	/**
	 * Creates an {@link RtActivity} that can control joint velocities.
	 * 
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling joint velocities
	 * @throws RoboticsException if joint velocity control is not possible
	 */
	JointJoggingActivity jointJogging(DeviceParameters... parameters) throws RoboticsException;
}
