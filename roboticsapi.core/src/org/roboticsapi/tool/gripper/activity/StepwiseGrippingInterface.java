/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.tool.gripper.GripperParameters;

/**
 * This gripping interface provides the basic position-based activities for
 * parallel grippers.
 */
public interface StepwiseGrippingInterface extends GrippingInterface {

	/**
	 * Creates an {@link RtActivity} for opening the gripper in order to reference
	 * the gripper's base jaws and fingers.
	 * <p>
	 * This activity has to be executed prior to any other position-based activity.
	 * During the reference movement, the gripper moves its fingers into the
	 * specified direction until it reaches its mechanical end stop. The blocking
	 * position is used as new origin for position-based commands.
	 * 
	 * @return the open activity for referencing.
	 * @throws RoboticsException if the requested reference activity cannot not be
	 *                           created.
	 */
	RtActivity openForReferencing() throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for closing the gripper in order to reference
	 * the gripper's base jaws and fingers.
	 * <p>
	 * This activity has to be executed prior to any other position-based activity.
	 * During the reference movement, the gripper moves its fingers into the
	 * specified direction until it reaches its mechanical end stop. The blocking
	 * position is used as new origin for position-based commands.
	 * 
	 * @return the close activity for referencing.
	 * @throws RoboticsException if the requested reference activity cannot not be
	 *                           created.
	 */
	RtActivity closeForReferencing() throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for opening the parallel gripper stepwise by
	 * specifying a relative opening width.
	 * 
	 * @param width      the desired (relative) width in [m] for opening the gripper
	 *                   stepwise. The given width is treated as an offset to the
	 *                   current opening width.
	 * @param parameters additional gripper parameters.
	 * @return the opening activity.
	 * @throws RoboticsException if the requested opening activity cannot not be
	 *                           created.
	 */
	RtActivity openStepwise(final double width, final GripperParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for closing the parallel gripper stepwise by
	 * specifying a relative opening width.
	 * 
	 * @param width      the desired (relative) width in [m] for closing the gripper
	 *                   stepwise. The given width is treated as an offset to the
	 *                   current opening width.
	 * @param parameters additional gripper parameters.
	 * @return the closing activity.
	 * @throws RoboticsException if the requested closing activity cannot not be
	 *                           created.
	 */
	RtActivity closeStepwise(final double width, final GripperParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for pre-positioning the parallel gripper by
	 * specifying a absolute opening width.
	 * 
	 * @param width      the desired (absolute) opening width in [m] between the
	 *                   gripper's fingers.
	 * @param parameters additional gripper parameters.
	 * @return the pre-positioning activity.
	 * @throws RoboticsException if the requested pre-positioning activity cannot
	 *                           not be created.
	 */
	RtActivity preposition(final double width, final GripperParameters... parameters) throws RoboticsException;

}
