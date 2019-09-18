/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * Manual jogging of robot joints.
 */
public class FollowJointVelocity extends Action implements GoalAction, JointSpaceAction {

	private final RealtimeDouble[] sensors;

	public FollowJointVelocity(double[] sensors) {
		this(toRealtimeValue(sensors));
	}

	private static RealtimeDouble[] toRealtimeValue(double[] sensors) {
		RealtimeDouble[] ret = new RealtimeDouble[sensors.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = RealtimeDouble.createFromConstant(sensors[i]);
		}
		return ret;
	}

	public FollowJointVelocity(RealtimeDouble[] sensors) {
		super(0, false, true);
		this.sensors = sensors;
	}

	public RealtimeDouble[] getVelocity() {
		return sensors;
	}

}
