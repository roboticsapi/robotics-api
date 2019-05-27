/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.MultiJointDevice;

/**
 * This interface holds functionality which can be executed on simulated joints
 * only.
 */
public interface SimulatedJointMotionInterface extends DeviceInterface {

	/**
	 * Creates an action which resets the position of all joints immediately. This
	 * results in a jump to the new positions.
	 * 
	 * @param newPositions the new positions for each joint of the
	 *                     {@link MultiJointDevice}
	 * @param parameters   optional device parameters
	 * @return the reset activity
	 * @throws RoboticsException the robotics exception
	 */
	public RtActivity resetJoints(double[] newPositions, DeviceParameters... parameters) throws RoboticsException;

}
