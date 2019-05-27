/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * Handle for controlling execution of a
 * {@link FollowJointGoalFromJavaActivity}.
 */
public interface FollowJointGoalHandle {

	/**
	 * Sets new rotation goal values for each joint.
	 * 
	 * @param goalValues new rotation goal for each axis
	 * @throws RoboticsException if handle is no longer valid (i.e. Activity
	 *                           execution has terminated)
	 */
	public void updateGoal(Double... goalValues) throws RoboticsException;

	/**
	 * Sets new rotation goal values for each joint.
	 * 
	 * @param goalValues new rotation goal for each axis
	 * @throws RoboticsException if handle is no longer valid (i.e. Activity
	 *                           execution has terminated)
	 */
	public void updateGoal(double[] goalValues) throws RoboticsException;

	/**
	 * Checks handle is valid.
	 * 
	 * @return true, if handle is valid
	 */
	public boolean isValid();

	/**
	 * Cancels execution of the {@link FollowJointGoalFromJavaActivity}.
	 * 
	 * @throws RoboticsException if cancelling failed
	 */
	public void cancelExecute() throws RoboticsException;
}
