/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.tool.gripper.Gripper;
import org.roboticsapi.tool.gripper.GripperParameters;

/**
 * The gripping interface provides the two basic activities, i.e. "open" and
 * "close", for grippers.
 */
public interface GrippingInterface extends DeviceInterface {

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
	RtActivity open(final GripperParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for simply closing a gripper.
	 * 
	 * @param parameters additional gripper parameters.
	 * @return the {@link RtActivity} for closing the gripper
	 * @throws RoboticsException if the requested activity could not be created
	 */
	RtActivity close(final GripperParameters... parameters) throws RoboticsException;

	@Override
	Gripper getDevice();

}
