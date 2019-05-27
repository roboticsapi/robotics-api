/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.cartesianmotion.action.CartesianJoggingHandle;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

/**
 * A {@link CartesianJoggingActivity} can be used to control cartesian
 * velocities of a certain {@link Frame}.
 */
public interface CartesianJoggingActivity extends RtActivity {

	/**
	 * Starts the jogging and returns a {@link CartesianJoggingHandle} that can set
	 * cartesian velocities of the joggedFrame (see
	 * {@link CartesianJoggingInterface}).
	 * 
	 * Calling this method has the same effect as calling beginExecute() and
	 * getHandle() subsequently.
	 * 
	 * @return The {@link CartesianJoggingHandle}
	 * @throws RoboticsException if cartesian jogging is not possible.
	 */
	CartesianJoggingHandle startJogging() throws RoboticsException;

	/**
	 * Gets the {@link CartesianJoggingHandle} of this
	 * {@link CartesianJoggingActivity} if it has been started already (by calling
	 * startJogging() or beginExecute()).
	 * 
	 * @return the {@link CartesianJoggingHandle}
	 */
	CartesianJoggingHandle getHandle();
}
