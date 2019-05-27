/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.cartesianmotion.action.LIN;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public class PositionControlMode implements ControlMode {

	private final double distance;

	public PositionControlMode(double distance) {
		this.distance = distance;
	}

	@Override
	public Action createAction(Frame baseFrame, Frame movingFrame, Frame taskFrame, Dimension d)
			throws RoboticsException {
		// if (distance == 0d) {
		// return new HoldCartesianPosition(taskFrame);
		// } else {
		Frame start = taskFrame.snapshot(baseFrame);
		return new LIN(start, createGoalFrame(start, d));
		// }
	}

	private Frame createGoalFrame(Frame start, Dimension d) {
		return start.plus(d.createTransformation(d, distance));
	}

}
