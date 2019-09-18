/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.WritableRealtimeDouble;

/**
 * Manual jogging of robot joints.
 */
public class JointJogging extends Action implements ProcessAction, JointSpaceAction {

	private final WritableRealtimeDouble[] sensors;

	public JointJogging(RoboticsRuntime runtime, final int jointCount) {
		super(0, false, true);

		sensors = new WritableRealtimeDouble[jointCount];

		for (int i = 0; i < jointCount; i++) {
			sensors[i] = RealtimeDouble.createWritable(0);
		}

	}

	public WritableRealtimeDouble[] getVelocity() {
		return sensors;
	}

}
