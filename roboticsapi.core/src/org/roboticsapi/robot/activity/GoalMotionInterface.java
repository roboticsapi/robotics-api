/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import org.roboticsapi.cartesianmotion.activity.CartesianGoalMotionInterface;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.multijoint.activity.JointGoalMotionInterface;

/**
 * DeviceInterface for motions that dynamically follow motion goals which may
 * change during execution.
 */
public interface GoalMotionInterface extends DeviceInterface, CartesianGoalMotionInterface, JointGoalMotionInterface {
}
