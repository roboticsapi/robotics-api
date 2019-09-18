/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public interface LinearMotionInterface extends ActuatorInterface {

	/**
	 * Gets the {@link Frame} used as motion center for all RtActivities created by
	 * this interface if no other {@link MotionCenterParameter} has been specified.
	 *
	 * @return the default motion center
	 */
	public abstract Pose getDefaultMotionCenter() throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a linear motion from the current cartesian
	 * position to a new Cartesian position specified by the given {@link Frame}.
	 *
	 * @param to         the Cartesian target position
	 * @param parameters optional DeviceParameters
	 * @return linear motion {@link RtActivity}
	 * @throws RoboticsException if the requested linear motion is not valid
	 */
	PlannedActivity lin(Pose to, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a linear motion from the current cartesian
	 * position to a new Cartesian position specified by the given {@link Frame}.
	 *
	 * @param to          the Cartesian target position
	 * @param speedFactor factor by which to scale motion speed (0..1)
	 * @param parameters  optional DeviceParameters
	 * @return linear motion {@link RtActivity}
	 * @throws RoboticsException if the requested linear motion is not valid
	 */
	PlannedActivity lin(Pose to, double speedFactor, DeviceParameters... parameters) throws RoboticsException;

}