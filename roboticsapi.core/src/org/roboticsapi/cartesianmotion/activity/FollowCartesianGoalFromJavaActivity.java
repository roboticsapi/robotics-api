/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * An RtActivity that lets the robot follow a given cartesian goal.
 */
public interface FollowCartesianGoalFromJavaActivity extends RtActivity {

	/**
	 * Start the Activity and returns a {@link FollowCartesianGoalHandle} for
	 * controlling execution.
	 * 
	 * @return handle for controlling execution
	 * @throws RoboticsException if Activity can not be started
	 */
	public FollowCartesianGoalHandle startFollowing() throws RoboticsException;

	/**
	 * Gets the {@link FollowCartesianGoalHandle} for controlling execution.
	 * 
	 * @return handle for controlling execution
	 */
	public FollowCartesianGoalHandle getHandle();
}
