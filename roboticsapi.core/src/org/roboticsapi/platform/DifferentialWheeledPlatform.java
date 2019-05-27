/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform;

import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;

/**
 * Base interface for differential wheeled platforms with two separately driven
 * wheels placed on either side of the platform.
 * 
 * @param <PD> the platform's driver
 */
public interface DifferentialWheeledPlatform extends Platform, CartesianMotionDevice {

	/**
	 * Constant identifier for left wheels.
	 */
	public static final int LEFT_WHEEL = 0;

	/**
	 * Constant identifier for right wheels.
	 */
	public static final int RIGHT_WHEEL = 1;

	@Override
	public Wheel getWheel(int wheelNumber);

	/**
	 * Returns the front left Mecanum wheel.
	 * 
	 * @return the front left Mecanum wheel.
	 */
	public Wheel getLeftWheel();

	/**
	 * Returns the front right Mecanum wheel.
	 * 
	 * @return the front right Mecanum wheel.
	 */
	public Wheel getRightWheel();

}
