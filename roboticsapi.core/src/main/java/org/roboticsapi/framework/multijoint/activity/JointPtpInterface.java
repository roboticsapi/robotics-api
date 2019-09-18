/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.robot.RobotArm;

public interface JointPtpInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current to
	 * a given joint configuration.
	 *
	 * @param to          the target joint configuration
	 * @param speedFactor factor by which to scale motion speed (0..1)
	 * @param parameters  optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedActivity ptp(final double[] to, double speedFactor, final DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current to
	 * a given joint configuration.
	 *
	 * @param to         the target joint configuration
	 * @param parameters optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedActivity ptp(final double[] to, final DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to the {@link RobotArm}'s home position.
	 *
	 * @param parameters the parameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException the robotics exception
	 */
	public abstract PlannedActivity ptpHome(final DeviceParameters... parameters) throws RoboticsException;

}
