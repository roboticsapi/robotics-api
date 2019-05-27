/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.AbstractJoint;

/**
 * A {@link JointJoggingHandle} can be used to set joint velocities of an
 * already running joint jogging.
 */
public interface JointJoggingHandle {

	/**
	 * Sets velocity of the {@link AbstractJoint} with the given number to the given
	 * value.
	 * 
	 * @param jointNumber the number of the joint whose velocity is to be set
	 * @param velocity    the velocity in rad/s
	 * @throws JointJoggingException if an error occurred during the jogging
	 * @throws RoboticsException
	 */
	public void setJointVelocity(final int jointNumber, final double velocity) throws RoboticsException;

	public void stopJogging() throws RoboticsException;

}
