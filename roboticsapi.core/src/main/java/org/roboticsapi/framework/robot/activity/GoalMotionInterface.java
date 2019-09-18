/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianGoalMotionInterface;
import org.roboticsapi.framework.multijoint.activity.JointGoalMotionInterface;

/**
 * DeviceInterface for motions that dynamically follow motion goals which may
 * change during execution.
 */
public interface GoalMotionInterface extends ActuatorInterface, CartesianGoalMotionInterface, JointGoalMotionInterface {
}
