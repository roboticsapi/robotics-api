/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.sensor.DoubleSensor;

/**
 * Driver interface for {@link Joint}s.
 */
public interface JointDriver extends ActuatorDriver {

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

}
