/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * An RtActivity that lets the robot axes follow given goal angles.
 */
public interface FollowJointGoalFromJavaActivity extends RtActivity {

	/**
	 * Start the Activity and returns a {@link FollowJointGoalHandle} for
	 * controlling execution.
	 * 
	 * @return handle for controlling execution
	 * @throws RoboticsException if Activity can not be started
	 */
	public FollowJointGoalHandle startFollowing() throws RoboticsException;

	/**
	 * Gets the {@link FollowJointGoalHandle} for controlling execution.
	 * 
	 * @return handle for controlling execution
	 */
	public FollowJointGoalHandle getHandle();
}
