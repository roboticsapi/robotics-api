/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;

/**
 * The {@link CartesianJoggingInterface} provides Activities for velocity-based
 * cartesian {@link RobotArm} motion control.
 */
public interface CartesianJoggingInterface extends DeviceInterface {

	/**
	 * Creates an {@link RtActivity} that can control cartesian velocities of
	 * {@link Frame} joggedFrame relative to {@link Frame} referenceFrame.
	 * 
	 * @param joggedFrame    the {@link Frame} whose velocity is controlled
	 * @param referenceFrame the {@link Frame} relative to which velocity is
	 *                       controlled
	 * @param guidanceFrame  a {@link Frame} whose axes determine the direction of
	 *                       the specified velocities
	 * @param parameters     optional DeviceParameters
	 * @return an {@link RtActivity} for controlling cartesian velocities
	 * @throws RoboticsException if cartesian jogging is not possible
	 */
	CartesianJoggingActivity cartesianJogging(final Frame joggedFrame, final Frame referenceFrame,
			final Point pivotPoint, Orientation orientation, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that can control cartesian velocities of a
	 * Device's effector {@link Frame} relative to its reference {@link Frame}.
	 * 
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling cartesian velocities
	 * @throws RoboticsException if cartesian jogging is not possible
	 */
	CartesianJoggingActivity cartesianJogging(DeviceParameters... parameters) throws RoboticsException;
}
