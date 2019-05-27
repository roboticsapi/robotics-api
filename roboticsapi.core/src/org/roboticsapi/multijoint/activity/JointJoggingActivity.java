/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.AbstractJoint;
import org.roboticsapi.multijoint.action.JointJoggingHandle;
import org.roboticsapi.robot.RobotArm;

/**
 * A {@link JointJoggingActivity} can be used to control velocities of
 * {@link RobotArm} {@link AbstractJoint}s.
 */
public interface JointJoggingActivity extends RtActivity {

	/**
	 * Starts the jogging and returns a {@link JointJoggingHandle} that can set
	 * {@link AbstractJoint} velocities.
	 * 
	 * Calling this method has the same effect as calling beginExecute() and
	 * getHandle() subsequently.
	 * 
	 * @return The {@link JointJoggingHandle}
	 * @throws RoboticsException if joint jogging is not possible.
	 */
	JointJoggingHandle startJogging() throws RoboticsException;

	/**
	 * Gets the {@link JointJoggingHandle} of this {@link JointJoggingActivity} if
	 * it has been started already (by calling startJogging() or beginExecute()).
	 * 
	 * @return the {@link JointJoggingHandle}
	 */
	JointJoggingHandle getHandle();
}
