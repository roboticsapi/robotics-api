/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper.activity;

import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.gripper.GripperParameters;

/**
 * This interface provides the sophisticated grasping activities for parallel
 * grippers.
 */
public interface GraspingInterface extends StepwiseGrippingInterface {

	/**
	 *
	 * @param width      of the part to grasp, in [m]
	 * @param parameters
	 * @return
	 * @throws RoboticsException
	 */
	Activity grasp(double width, final GripperParameters... parameters) throws RoboticsException;

	//
	// RtActivity grasp(double width, double tolerance,
	// final GripperParameters... parameters) throws RoboticsException;

	/**
	 *
	 * @param width
	 * @param parameters
	 * @return
	 * @throws RoboticsException
	 */
	Activity release(double width, final GripperParameters... parameters) throws RoboticsException;

}
