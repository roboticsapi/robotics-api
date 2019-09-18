/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot;

import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

/**
 * Exception occurring in case of any error during controlling this
 * {@link RobotArm}.
 */
public class RobotException extends ActuatorDriverRealtimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public RobotException(RobotArmDriver robotArmDriver) {
		super(robotArmDriver);
	}

}