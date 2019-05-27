/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.sensor.TransformationSensor;

/**
 * This interface represent a joint (i.e. controllable axis, e.g. revolute or
 * prismatic).
 */
public interface Joint extends Actuator {

	/**
	 * Retrieves the fixed frame of the joint.
	 *
	 * @return the fixed frame
	 */
	Frame getFixedFrame();

	/**
	 * Retrieves the moving frame of the joint.
	 *
	 * @return the moving frame
	 */
	Frame getMovingFrame();

	/**
	 * Retrieves the joint connection (between fixed and moving frame)
	 */
	JointConnection getJointConnection();

	/**
	 * Retrieves the maximum acceleration.
	 *
	 * @return maximum acceleration of the joint
	 */
	double getMaximumAcceleration();

	/**
	 * Retrieves the maximum jerk.
	 *
	 * @return maximum jerk of the joint
	 */
	double getMaximumJerk();

	/**
	 * Retrieves the maximum velocity.
	 *
	 * @return maximum velocity of the joint
	 */
	double getMaximumVelocity();

	/**
	 * Retrieves the minimum position.
	 *
	 * @return minimum position of the joint
	 */
	double getMinimumPosition();

	/**
	 * Retrieves the maximum position.
	 *
	 * @return maximum position of the joint
	 */
	double getMaximumPosition();

	/**
	 * Retrieves the home position of the joint.
	 *
	 * @return home position of the joint
	 */
	double getHomePosition();

	/**
	 * Retrieves the commanded position of the joint.
	 *
	 * @return the currently commanded position of the joint.
	 * @throws RoboticsException if the value can not be determined
	 */
	double getCommandedPosition() throws RoboticsException;

	/**
	 * Retrieves the measured position of the joint.
	 *
	 * @return the currently measured position of the joint.
	 * @throws RoboticsException if the value can not be determined
	 */
	double getMeasuredPosition() throws RoboticsException;

	/**
	 * Retrieves a sensor for the (commanded) value of the joint.
	 *
	 * @return The joint's commanded position sensor.
	 */
	DoubleSensor getCommandedPositionSensor();

	/**
	 * Retrieves a sensor for the (measured) value of the joint.
	 *
	 * @return The joint's measured position sensor.
	 */
	DoubleSensor getMeasuredPositionSensor();

	/**
	 * Retrieves a sensor for the (commanded) velocity value of the joint.
	 *
	 * @return The joint's commanded velocity sensor.
	 */
	DoubleSensor getCommandedVelocitySensor();

	/**
	 * Retrieves a sensor for the (measured) velocity value of the joint.
	 *
	 * @return The joint's measured velocity sensor.
	 */
	DoubleSensor getMeasuredVelocitySensor();

	/**
	 * Retrieves a forward kinematics sensor to calculate the transformation of the
	 * joint based on the joint position given by a double sensor.
	 *
	 * @param position joint position
	 * @return transformation for the given position
	 */
	TransformationSensor getForwardKinematicsSensor(DoubleSensor position);

	@Override
	public JointDriver getDriver();

}