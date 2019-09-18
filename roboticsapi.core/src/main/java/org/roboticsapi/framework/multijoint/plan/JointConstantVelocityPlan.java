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

public class JointConstantVelocityPlan implements ExecutableJointMotionPlan {
	private final double[] start;
	private final double[] vel;
	private final double starttime;

	public JointConstantVelocityPlan(double[] startpos, double[] vel, double starttime) {
		this.start = startpos;
		this.vel = vel;
		this.starttime = starttime;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = start[i] + (time - starttime) * vel[i];
		}
		return ret;
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = vel[i];
		}
		return ret;
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = 0;
		}
		return ret;
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
		RealtimeDouble[] ret = new RealtimeDouble[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = time.add(-starttime).multiply(vel[i]).add(start[i]);
		}
		return ret;
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
	}
}
