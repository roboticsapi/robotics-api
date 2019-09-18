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
 * The LinInterface provides different variants of linear cartesian motions
 * (LIN) in form of RtActivities.
 */
public interface RobotLinearMotionInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} for a linear motion from the current cartesian
	 * position to a new Cartesian position specified by the given {@link Frame}.
	 *
	 * @param to              the Cartesian target position
	 * @param nullspaceJoints set of joints serving as redundancy parameters
	 * @param parameters      optional DeviceParameters
	 * @return linear motion {@link RtActivity}
	 * @throws RoboticsException if the requested linear motion is not valid
	 */
	public abstract PlannedActivity lin(final Pose to, double[] nullspaceJoints, final DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a linear motion from the current cartesian
	 * position to a new Cartesian position specified by the given {@link Frame}.
	 *
	 * @param to              the Cartesian target position
	 * @param nullspaceJoints set of joints serving as redundancy parameters
	 * @param speedFactor     factor by which to scale motion speed (0..1)
	 * @param parameters      optional DeviceParameters
	 * @return linear motion {@link RtActivity}
	 * @throws RoboticsException if the requested linear motion is not valid
	 */
	public abstract PlannedActivity lin(final Pose to, double[] nullspaceJoints, double speedFactor,
			final DeviceParameters... parameters) throws RoboticsException;

}