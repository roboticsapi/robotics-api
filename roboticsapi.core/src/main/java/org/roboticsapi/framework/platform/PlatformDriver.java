/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;

/**
 * Device driver interface for mobile platforms.
 */
public interface PlatformDriver extends ActuatorDriver {

	Relation createMeasuredOdometryRelation(Frame from, Frame to);

	Relation createCommandedOdometryRelation(Frame from, Frame to);

	/**
	 * Returns a {@link RealtimeDouble} for the position of the <code>i</code>th
	 * wheel.
	 *
	 * @param i the wheel number
	 * @return a double sensor for the wheel's position.
	 */
	RealtimeDouble getWheelPositionSensor(int i);

	/**
	 * Returns a {@link RealtimeDouble} for the velocity of the <code>i</code>th
	 * wheel.
	 *
	 * @param i the wheel number
	 * @return a double sensor for the wheel's velocity.
	 */
	RealtimeDouble getWheelVelocitySensor(int i);
}
