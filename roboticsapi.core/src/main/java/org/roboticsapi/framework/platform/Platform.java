/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;

/**
 * Base interface for mobile robot platforms.
 * 
 * @param <PD> the platform's driver
 */
public interface Platform extends Actuator, PhysicalObject {

	/**
	 * The origin frame of the platform (intial position)
	 */
	Frame getOdometryOrigin();

	/**
	 * The frame moved by the platform (center of odometry)
	 */
	Frame getOdometryFrame();

	/**
	 * Gets the {@link Wheel} with the given number (zero-based).
	 * 
	 * @param wheelNumber the wheel number
	 * @return the wheel
	 */
	Wheel getWheel(int wheelNumber);

	/**
	 * Gets all of this Robot's {@link Wheel}s.
	 * 
	 * @return the wheel
	 */
	Wheel[] getWheels();

	/**
	 * Gets the mount {@link Frame} for the {@link Wheel} with the given number
	 * (zero-based).
	 * 
	 * @param wheelNumber the wheel number
	 * @return the mount frame
	 */
	Frame getMountFrame(int wheelNumber);

	/**
	 * Gets the mount {@link Frame}s for all {@link Wheel}s.
	 * 
	 * @param wheelNumber the wheel number
	 * @return the mount frame
	 */
	Frame[] getMountFrames();

	@Override
	PlatformDriver getDriver();

}
