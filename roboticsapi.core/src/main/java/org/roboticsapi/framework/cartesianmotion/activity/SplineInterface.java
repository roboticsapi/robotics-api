/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;

/**
 * The Interface SplineInterface creates RtActivities for performing spline
 * motions.
 */
public interface SplineInterface extends ActuatorInterface {

	Pose getDefaultMotionCenter() throws RoboticsException;

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
	CartesianSplineActivity spline(double speedFactor, DeviceParameters[] parameters, final Pose splinePoint,
			final Pose... furtherSplinePoints) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that performs a spline motion. The spline curve
	 * is defined by a series of Frames.
	 *
	 * @param splinePoint         first spline point
	 * @param furtherSplinePoints further spline points
	 * @return the RtActivity performing spline motion
	 * @throws RoboticsException thrown if the RtActivity could not be created
	 */
	CartesianSplineActivity spline(final Pose splinePoint, final Pose... furtherSplinePoints) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that performs a spline motion. The spline curve
	 * is defined by a series of Frames.
	 *
	 * @param speedFactor         velocity scaling factor [0..1]
	 * @param splinePoint         first spline point
	 * @param furtherSplinePoints further spline points
	 * @return the RtActivity performing spline motion
	 * @throws RoboticsException thrown if the RtActivity could not be created
	 */
	CartesianSplineActivity spline(double speedFactor, final Pose splinePoint, final Pose... furtherSplinePoints)
			throws RoboticsException;

}
