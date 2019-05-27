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
import org.roboticsapi.world.Frame;

/**
 * The Interface SplineInterface creates RtActivities for performing spline
 * motions.
 */
public interface SplineInterface extends DeviceInterface {
	Frame getDefaultMotionCenter();

	/**
	 * Creates an {@link RtActivity} that performs a spline motion. The spline curve
	 * is defined by a series of Frames.
	 * 
	 * @param parameters          {@link DeviceParameters} to employ for the motion
	 * @param splinePoint         first spline point
	 * @param furtherSplinePoints further spline points
	 * @return the RtActivity performing spline motion
	 * @throws RoboticsException thrown if the RtActivity could not be created
	 */
	CartesianSplineActivity spline(DeviceParameters[] parameters, final Frame splinePoint,
			final Frame... furtherSplinePoints) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that performs a spline motion. The spline curve
	 * is defined by a series of Frames.
	 * 
	 * @param splinePoint         first spline point
	 * @param furtherSplinePoints further spline points
	 * @return the RtActivity performing spline motion
	 * @throws RoboticsException thrown if the RtActivity could not be created
	 */
	CartesianSplineActivity spline(final Frame splinePoint, final Frame... furtherSplinePoints)
			throws RoboticsException;
}
