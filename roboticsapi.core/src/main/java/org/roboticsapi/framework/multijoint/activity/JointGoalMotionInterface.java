/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public interface JointGoalMotionInterface extends ActuatorInterface {

	/**
	 * Creates an Activity that lets the robot axes follow given goal angles. The
	 * goal angles are provided by a set of RealtimeDoubles, one for each axis.
	 *
	 * @param goal       Sensors specifying the goal angle for each axis
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot follow the given joint goals
	 * @throws RoboticsException if Activity could not be created
	 */
	Activity followJointGoal(RealtimeDouble[] goal, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an Activity that lets the robot axes move to given goal angles. The
	 * goal angles are provided by a set of DoubleSensors, one for each axis.
	 *
	 * @param goal       Sensors specifying the goal angle for each axis
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot move to the given joint goals
	 * @throws RoboticsException if Activity could not be created
	 */
	Activity moveToJointGoal(RealtimeDouble[] goal, DeviceParameters... parameters) throws RoboticsException;

	Activity moveToJointGoal(double[] goal, DeviceParameters... parameters) throws RoboticsException;

}