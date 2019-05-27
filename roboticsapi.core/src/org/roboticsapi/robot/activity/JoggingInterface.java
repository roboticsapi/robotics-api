/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import java.awt.Robot;

import org.roboticsapi.cartesianmotion.activity.CartesianJoggingInterface;
import org.roboticsapi.multijoint.activity.JointJoggingInterface;

/**
 * The JoggingInterface offers functionality for velocity-based {@link Robot}
 * control.
 */
public interface JoggingInterface extends CartesianJoggingInterface, JointJoggingInterface {

}
