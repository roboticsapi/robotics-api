/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Transformation;

/**
 * Handle for controlling execution of a
 * {@link FollowCartesianGoalFromJavaActivity}
 */
public interface FollowCartesianGoalHandle {

	/**
	 * Sets new Cartesian goal values. Values are interpreted relative to the
	 * reference {@link} that was specified during creation of the
	 * {@link FollowCartesianGoalFromJavaActivity}.
	 * 
	 * @param x the new x translation
	 * @param y the new y translation
	 * @param z the new z translation
	 * @param a the new a rotation
	 * @param b the new b rotation
	 * @param c the new c rotation
	 * @throws RoboticsException if handle is no longer valid (i.e. Activity
	 *                           execution has terminated)
	 */
	public void updateGoal(double x, double y, double z, double a, double b, double c) throws RoboticsException;

	/**
	 * Sets a new Cartesian goal transformation. Transformation is interpreted
	 * relative to the reference {@link} that was specified during creation of the
	 * {@link FollowCartesianGoalFromJavaActivity}.
	 * 
	 * @param trans the new transformation
	 * @throws RoboticsException if handle is no longer valid (i.e. Activity
	 *                           execution has terminated)
	 */
	public void updateGoal(Transformation trans) throws RoboticsException;

	/**
	 * Checks handle is valid.
	 * 
	 * @return true, if handle is valid
	 */
	public boolean isValid();

	/**
	 * Cancels execution of the {@link FollowCartesianGoalFromJavaActivity}.
	 * 
	 * @throws RoboticsException if cancelling failed
	 */
	public void cancelExecute() throws RoboticsException;
}
