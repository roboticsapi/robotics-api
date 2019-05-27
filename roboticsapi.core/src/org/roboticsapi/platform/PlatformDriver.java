/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;

/**
 * Device driver interface for mobile platforms.
 */
public interface PlatformDriver extends ActuatorDriver {

	void setup(Frame odometryOrigin, Frame odometryFrame);

	/**
	 * The origin frame of the platform (intial position)
	 */
	Frame getOdometryOrigin();

	/**
	 * The frame moved by the platform (center of odometry)
	 */
	Frame getOdometryFrame();

	Relation createNewOdometryRelation();

	/**
	 * Returns a {@link DoubleSensor} for the position of the <code>i</code>th
	 * wheel.
	 * 
	 * @param i the wheel number
	 * @return a double sensor for the wheel's position.
	 */
	DoubleSensor getWheelPositionSensor(int i);

	/**
	 * Returns a {@link DoubleSensor} for the velocity of the <code>i</code>th
	 * wheel.
	 * 
	 * @param i the wheel number
	 * @return a double sensor for the wheel's velocity.
	 */
	DoubleSensor getWheelVelocitySensor(int i);
}
