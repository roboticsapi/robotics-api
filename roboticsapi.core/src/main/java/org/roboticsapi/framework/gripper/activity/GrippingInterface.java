/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper.activity;

import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.gripper.GripperParameters;

/**
 * The gripping interface provides the two basic activities, i.e. "open" and
 * "close", for grippers.
 */
public interface GrippingInterface extends ActuatorInterface {

	enum GrippingDirection {
		OPEN, CLOSE;
	}

	/**
	 * Creates an {@link RtActivity} for simply opening a gripper.
	 *
	 * @param parameters additional gripper parameters.
	 * @return the {@link RtActivity} for opening the gripper
	 * @throws RoboticsException if the requested activity could not be created
	 */
	Activity open(final GripperParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for simply closing a gripper.
	 *
	 * @param parameters additional gripper parameters.
	 * @return the {@link RtActivity} for closing the gripper
	 * @throws RoboticsException if the requested activity could not be created
	 */
	Activity close(final GripperParameters... parameters) throws RoboticsException;

}
