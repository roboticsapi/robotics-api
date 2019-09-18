/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint;

import org.roboticsapi.core.SensorInterface;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public interface JointSensorInterface extends SensorInterface {

	/**
	 * Retrieves a sensor for the (commanded) value of the joint.
	 *
	 * @return The joint's commanded position sensor.
	 */
	RealtimeDouble getCommandedPositionSensor();

	/**
	 * Retrieves a sensor for the (measured) value of the joint.
	 *
	 * @return The joint's measured position sensor.
	 */
	RealtimeDouble getMeasuredPositionSensor();

	/**
	 * Retrieves a sensor for the (commanded) velocity value of the joint.
	 *
	 * @return The joint's commanded velocity sensor.
	 */
	RealtimeDouble getCommandedVelocitySensor();

	/**
	 * Retrieves a sensor for the (measured) velocity value of the joint.
	 *
	 * @return The joint's measured velocity sensor.
	 */
	RealtimeDouble getMeasuredVelocitySensor();

}
