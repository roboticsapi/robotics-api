/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;

public interface CartesianGoalMotionInterface extends ActuatorInterface {

	/**
	 * Creates an Activity that lets the robot follow a given cartesian motion goal.
	 * The goal is specified directly as a Frame (that might be moving).
	 *
	 * @param goal       the goal that is followed
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot follow the given goal
	 * @throws RoboticsException if Activity could not be created
	 */
	Activity followCartesianGoal(Pose goal, DeviceParameters... parameters) throws RoboticsException;

	Activity followCartesianGoal(RealtimePose goal, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an Activity that lets the robot move to a given cartesian motion
	 * goal. The goal is specified directly as a Frame (that might be moving).
	 *
	 * @param goal       the goal the robot is moved to
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot move to the given goal
	 * @throws RoboticsException if Activity could not be created
	 */
	Activity moveToCartesianGoal(Pose goal, DeviceParameters... parameters) throws RoboticsException;

	Activity moveToCartesianGoal(RealtimePose goal, DeviceParameters... parameters) throws RoboticsException;

}