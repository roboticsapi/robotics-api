/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.multijoint.MultiJointDevice;

/**
 * This interface holds functionality which can be executed on simulated joints
 * only.
 */
public interface SimulatedJointMotionInterface extends ActuatorInterface {

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
	Activity resetJoints(double[] newPositions, DeviceParameters... parameters) throws RoboticsException;

}
