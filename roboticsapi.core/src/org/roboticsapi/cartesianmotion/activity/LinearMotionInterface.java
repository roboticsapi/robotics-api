/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public interface LinearMotionInterface extends DeviceInterface {

	/**
	 * Gets the {@link Frame} used as motion center for all RtActivities created by
	 * this interface if no other {@link MotionCenterParameter} has been specified.
	 * 
	 * @return the default motion center
	 */
	public abstract Frame getDefaultMotionCenter();

	/**
	 * Creates an {@link RtActivity} for a linear motion from the current cartesian
	 * position to a new Cartesian position specified by the given {@link Frame}.
	 * 
	 * @param to         the Cartesian target position
	 * @param parameters optional DeviceParameters
	 * @return linear motion {@link RtActivity}
	 * @throws RoboticsException if the requested linear motion is not valid
	 */
	public abstract PlannedRtActivity lin(Frame to, DeviceParameters... parameters) throws RoboticsException;

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
	public abstract PlannedRtActivity lin(Frame to, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException;

}