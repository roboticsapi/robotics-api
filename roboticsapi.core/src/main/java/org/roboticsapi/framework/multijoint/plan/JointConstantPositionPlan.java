/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.plan;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.multijoint.action.ExecutableJointMotionPlan;

public class JointConstantPositionPlan implements ExecutableJointMotionPlan {

	private final double[] pos;

	public JointConstantPositionPlan(double[] startpos) {
		this.pos = startpos;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		return pos;
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		return new double[pos.length];
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		return new double[pos.length];
	}

	@Override
	public double getTotalTime() {
		return 0;
	}

	@Override
	public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
		return null;
	}

	@Override
	public RealtimeDouble[] getJointPositionSensorAt(RealtimeDouble time) {
		RealtimeDouble[] ret = new RealtimeDouble[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = RealtimeDouble.createFromConstant(pos[i]);
		}
		return ret;
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
	}
}
