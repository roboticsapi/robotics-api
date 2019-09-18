/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;

/**
 * The PtpInterface provides different variants of Point-To-Point (PTP) motions
 * in form of Activities.
 */
public interface RobotPtpInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to a new Cartesian position specified by the given
	 * {@link Frame}.
	 *
	 * @param to          the Cartesian target position
	 * @param speedFactor factor by which to scale motion speed (0..1) *
	 * @param hintJoints  a 'hint' to the preferred joint configuration to take at
	 *                    the target position; if multiple joint configurations
	 *                    exist, the one with the smallest distance to these hint
	 *                    joints will be chosen
	 * @param parameters  optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedActivity ptp(final Pose to, final double[] hintJoints, double speedFactor,
			final DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to a new Cartesian position specified by the given
	 * {@link Frame}.
	 *
	 * @param to         the Cartesian target position
	 * @param hintJoints a 'hint' to the preferred joint configuration to take at
	 *                   the target position; if multiple joint configurations
	 *                   exist, the one with the smallest distance to these hint
	 *                   joints will be chosen
	 * @param parameters optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedActivity ptp(final Pose to, final double[] hintJoints, final DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to a new Cartesian position specified by the given
	 * {@link Frame}.
	 *
	 * @param to         the Cartesian target position
	 * @param parameters optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedActivity ptp(final Pose to, final DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to a new Cartesian position specified by the given
	 * {@link Frame}.
	 *
	 * @param to          the Cartesian target position
	 * @param speedFactor factor by which to scale motion speed (0..1)
	 * @param parameters  optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedActivity ptp(final Pose to, double speedFactor, final DeviceParameters... parameters)
			throws RoboticsException;

}