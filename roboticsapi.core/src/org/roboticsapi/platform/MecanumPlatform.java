/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform;

import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;

/**
 * Base interface for omnimove platforms with four Mecanum wheels.
 * 
 * @param <PD> the platform's driver
 */
public interface MecanumPlatform extends Platform, CartesianMotionDevice {

	/**
	 * Constant identifier for front left wheels.
	 */
	public static final int FRONT_LEFT_WHEEL = 0;

	/**
	 * Constant identifier for front right wheels.
	 */
	public static final int FRONT_RIGHT_WHEEL = 1;

	/**
	 * Constant identifier for back left wheels.
	 */
	public static final int BACK_LEFT_WHEEL = 2;

	/**
	 * Constant identifier for back right wheels.
	 */
	public static final int BACK_RIGHT_WHEEL = 3;

	@Override
	public MecanumWheel getWheel(int wheelNumber);

	/**
	 * Returns the front left Mecanum wheel.
	 * 
	 * @return the front left Mecanum wheel.
	 */
	public MecanumWheel getFrontLeftWheel();

	/**
	 * Returns the front right Mecanum wheel.
	 * 
	 * @return the front right Mecanum wheel.
	 */
	public MecanumWheel getFrontRightWheel();

	/**
	 * Returns the back left Mecanum wheel.
	 * 
	 * @return the back left Mecanum wheel.
	 */
	public MecanumWheel getBackLeftWheel();

	/**
	 * Returns the back right Mecanum wheel.
	 * 
	 * @return the back right Mecanum wheel.
	 */
	public MecanumWheel getBackRightWheel();

}
