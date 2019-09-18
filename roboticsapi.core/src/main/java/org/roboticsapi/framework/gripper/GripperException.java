/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

/**
 * Exception for grippers.
 */
public class GripperException extends ActuatorDriverRealtimeException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7900447631442329672L;

	/**
	 * Constructor.
	 *
	 * @param gripperDriver the driver.
	 */
	public GripperException(GripperDriver gripperDriver) {
		super(gripperDriver);
	}
}