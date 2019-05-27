/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;

public interface JointGoalMotionInterface extends DeviceInterface {

	/**
	 * Creates an Activity that lets the robot axes follow given goal angles. The
	 * goal angles can be updated by using methods of the created
	 * {@link FollowJointGoalFromJavaActivity}.
	 *
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot follow the given joint goals
	 * @throws RoboticsException if Activity could not be created
	 */
	public abstract FollowJointGoalFromJavaActivity followJointGoalFromJava(DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Creates an Activity that lets the robot axes follow given goal angles. The
	 * goal angles are provided by a set of DoubleSensors, one for each axis.
	 *
	 * @param sensors    Sensors specifying the goal angle for each axis
	 * @param parameters parameters for Activity execution
	 * @return Activity that lets the robot follow the given joint goals
	 * @throws RoboticsException if Activity could not be created
	 */
	public abstract RtActivity followJointGoal(DoubleSensor[] sensors, DeviceParameters... parameters)
			throws RoboticsException;

	public abstract RtActivity followJointVelocity(DoubleSensor[] sensors, DeviceParameters... parameters)
			throws RoboticsException;

	public abstract RtActivity followJointVelocity(DoubleSensor[] sensors, double[] limits,
			DeviceParameters... parameters) throws RoboticsException;

}