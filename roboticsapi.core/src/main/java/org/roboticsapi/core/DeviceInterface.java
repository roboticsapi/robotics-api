/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * An interface for hardware independent and transparent device control
 */
public interface DeviceInterface {

	/**
	 * @return device attached to this concrete interface
	 */
	Device getDevice();

	/**
	 * @return {@link RoboticsRuntime} controlling the attached device
	 */
	RoboticsRuntime getRuntime();

}