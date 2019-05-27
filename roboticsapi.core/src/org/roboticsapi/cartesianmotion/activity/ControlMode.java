/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public interface ControlMode {
	Action createAction(Frame baseFrame, Frame movingFrame, Frame taskFrame, Dimension d) throws RoboticsException;
}
