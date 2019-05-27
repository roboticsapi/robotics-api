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
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.sensor.DirectionSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public interface CartesianVelocityMotionInterface extends DeviceInterface {

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame.
	 * 
	 * @param velocity   the velocity that should be applied
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	RtActivity moveVelocity(VelocitySensor velocity, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame.
	 * 
	 * @param direction  the direction the frame should move into
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	RtActivity moveVelocity(DirectionSensor direction, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame.
	 * 
	 * @param direction  the direction the frame should move into
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	RtActivity moveVelocity(Direction direction, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame.
	 * 
	 * @param direction  the direction the frame should move into
	 * @param rotation   the rotation the frame should apply
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	RtActivity moveVelocity(DirectionSensor direction, DirectionSensor rotation, DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame.
	 * 
	 * @param direction  the direction the frame should move into
	 * @param rotation   the rotation the frame should apply
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	RtActivity moveVelocity(Direction direction, Direction rotation, DeviceParameters... parameters)
			throws RoboticsException;

}
