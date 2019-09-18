/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.roboticsapi.framework.cartesianmotion.activity.CartesianControllerInterface;
import org.roboticsapi.framework.cartesianmotion.controller.CartesianController;
import org.roboticsapi.framework.multijoint.activity.JointControllerInterface;
import org.roboticsapi.framework.multijoint.controller.JointController;
import org.roboticsapi.framework.robot.RobotArm;

/**
 * ActuatorInterface for switching {@link JointController}s and
 * {@link CartesianController}s.
 */
public interface RobotControllerInterface extends JointControllerInterface, CartesianControllerInterface {

	@Override
	RobotArm getDevice();
}
