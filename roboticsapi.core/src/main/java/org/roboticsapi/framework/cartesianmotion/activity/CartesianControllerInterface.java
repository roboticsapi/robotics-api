/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.cartesianmotion.controller.CartesianController;

/**
 * ActuatorInterface for switching {@link CartesianController}s.
 */
public interface CartesianControllerInterface extends ActuatorInterface {
	/**
	 * Creates an {@link RtActivity} that switches the Actuator to the given
	 * {@link CartesianController}.
	 *
	 * @param controller the controller
	 * @param parameters (optional) parameters for Activity execution
	 * @return RtActivity for switching to the controller
	 * @throws RoboticsException if RtActivity construction fails
	 */
	Activity switchCartesianController(CartesianController controller, DeviceParameters... parameters)
			throws RoboticsException;

}
