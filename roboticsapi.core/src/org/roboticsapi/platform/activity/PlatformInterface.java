/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;

/**
 * Provides Activities for movement of robotic platforms.
 */
public interface PlatformInterface extends DeviceInterface {
	/**
	 * Drive to a given Frame, considering the position of the Frame at the current
	 * point in time.
	 * 
	 * @param goal       the target frame
	 * @param parameters parameters for movement
	 * @return Activity that drives to the goal
	 * @throws RoboticsException if Activity could not be created
	 */
	RtActivity driveTo(Frame goal, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Drive in a given direction with velocities determined by the given Sensors.
	 * 
	 * @param orientation the direction to drive to
	 * @param xVel        x velocity
	 * @param yVel        y velocity
	 * @param aVel        a velocity
	 * @param parameters  parameters for movement
	 * @return Activity that drives into the given direction
	 * @throws RoboticsException if Activity could not be created
	 */
	RtActivity driveDirection(Orientation orientation, DoubleSensor xVel, DoubleSensor yVel, DoubleSensor aVel,
			DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Drive in a given direction with the given velocities.
	 * 
	 * @param orientation the direction to drive to
	 * @param xVel        x velocity
	 * @param yVel        y velocity
	 * @param aVel        a velocity
	 * @param parameters  parameters for movement
	 * @return Activity that drives into the given direction
	 * @throws RoboticsException if Activity could not be created
	 */
	RtActivity driveDirection(Orientation orientation, double xVel, double yVel, double aVel,
			DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Drive in the direction of the platform odometry frame with velocities
	 * determined by the given Sensors.
	 * 
	 * @param orientation the direction to drive to
	 * @param xVel        x velocity
	 * @param yVel        y velocity
	 * @param aVel        a velocity
	 * @param parameters  parameters for movement
	 * @return Activity that drives into the given direction
	 * @throws RoboticsException if Activity could not be created
	 */
	RtActivity driveDirection(DoubleSensor xVel, DoubleSensor yVel, DoubleSensor aVel, DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Drive in the direction of the platform odometry frame with the given
	 * velocities.
	 * 
	 * @param xVel       x velocity
	 * @param yVel       y velocity
	 * @param aVel       a velocity
	 * @param parameters parameters for movement
	 * @return Activity that drives into the given direction
	 * @throws RoboticsException if Activity could not be created
	 */
	RtActivity driveDirection(double xVel, double yVel, double aVel, DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Follows a given Frame goal, considering eventual movements of that Frame.
	 * 
	 * @param goal       the goal to follow
	 * @param parameters parameters for movement
	 * @return Activity that follows the given goal
	 * @throws RoboticsException if Activity could not be created
	 */
	RtActivity followGoal(Frame goal, DeviceParameters... parameters) throws RoboticsException;
}
