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

public interface CartesianGoalMotionInterface extends DeviceInterface {

	/**
	 * Creates an Activity that lets the robot follow a given cartesian motion goal.
	 * The goal can be updated component-wise by using methods of the created
	 * {@link FollowCartesianGoalFromJavaActivity}. The specified values are
	 * interpreted relative to a given reference Frame.
	 * 
	 * @param reference  the reference Frame relative to which goal values are
	 *                   interpreted
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot follow a given goal
	 * @throws RoboticsException if Activity could not be created
	 */
	public abstract FollowCartesianGoalFromJavaActivity followCartesianGoalFromJava(Frame reference,
			DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an Activity that lets the robot follow a given cartesian motion goal.
	 * The goal is specified directly as a Frame (that might be moving).
	 * 
	 * @param goal       the goal that is followed
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot follow the given goal
	 * @throws RoboticsException if Activity could not be created
	 */
	public abstract RtActivity followCartesianGoal(Frame goal, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an Activity that lets the robot move to a given cartesian motion
	 * goal. The goal is specified directly as a Frame (that might be moving).
	 * 
	 * @param goal       the goal the robot is moved to
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot move to the given goal
	 * @throws RoboticsException if Activity could not be created
	 */
	public abstract RtActivity moveToCartesianGoal(Frame goal, DeviceParameters... parameters) throws RoboticsException;

}