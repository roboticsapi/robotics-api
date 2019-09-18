/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multicopter;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;

public interface QuadcopterInterface extends ActuatorInterface {

	/**
	 * Sends an arming command to the controlled multicopter, which allows sending
	 * motion commands to it thereafter
	 *
	 * @param parameters Optional custom DeviceParameters (or subclasses thereof)
	 *                   which override the default parameters of the device
	 * 
	 * @return Activity which arms the multicopter
	 * 
	 * @throws RoboticsException if Activity could not be created
	 */
	Activity arm(DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Sends a disarming command to the controlled multicopter, which prohibits
	 * sending further motion commands to it thereafter
	 *
	 * @param parameters Optional custom DeviceParameters (or subclasses thereof)
	 *                   which override the default parameters of the device
	 * 
	 * @return Activity which disarms the multicopter
	 * 
	 * @throws RoboticsException if Activity could not be created
	 */
	Activity disarm(DeviceParameters... parameters) throws RoboticsException;

}
