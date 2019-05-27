/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.robot.RobotArm;

public interface JointPtpInterface extends DeviceInterface {

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current to
	 * a given joint configuration.
	 * 
	 * @param to         the target joint configuration
	 * @param parameters optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedRtActivity ptp(final double[] to, final DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to the {@link RobotArm}'s home position.
	 * 
	 * @param parameters the parameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException the robotics exception
	 */
	public abstract PlannedRtActivity ptpHome(final DeviceParameters... parameters) throws RoboticsException;

}
