/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.tool.gripper.Gripper;
import org.roboticsapi.tool.gripper.GripperParameters;

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
	RtActivity grasp(double width, final GripperParameters... parameters) throws RoboticsException;

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
	RtActivity release(double width, final GripperParameters... parameters) throws RoboticsException;

	@Override
	Gripper getDevice();

}
