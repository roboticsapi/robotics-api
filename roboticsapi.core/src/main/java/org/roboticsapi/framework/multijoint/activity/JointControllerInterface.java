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
import org.roboticsapi.framework.multijoint.controller.JointController;

/**
 * ActuatorInterface for switching {@link JointController}s.
 */
public interface JointControllerInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} that switches the Actuator to the given
	 * {@link JointController}.
	 *
	 * @param controller the controller
	 * @param parameters (optional) parameters for Activity execution
	 * @return RtActivity for switching to the controller
	 * @throws RoboticsException if Activity construction fails
	 */
	public Activity switchJointController(JointController controller, DeviceParameters... parameters)
			throws RoboticsException;

}
