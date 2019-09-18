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

public class JointConstantAccelerationPlan implements ExecutableJointMotionPlan {

	private final double[] startpos;
	private final double[] startvel;
	private final double[] acc;
	private final double starttime;

	public JointConstantAccelerationPlan(double[] startpos, double[] startvel, double[] acc, double starttime) {
		this.startpos = startpos;
		this.startvel = startvel;
		this.acc = acc;
		this.starttime = starttime;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		double[] ret = new double[startpos.length];
		double t = time - starttime;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = startpos[i] + t * startvel[i] + t * t * acc[i] / 2;
		}
		return ret;
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		double[] ret = new double[startpos.length];
		double t = time - starttime;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = startvel[i] + acc[i] * t;
		}
		return ret;
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		double[] ret = new double[startpos.length];
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = acc[i];
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
		RealtimeDouble[] ret = new RealtimeDouble[startpos.length];
		RealtimeDouble t = time.add(-starttime);
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = t.multiply(startvel[i]).add(t.multiply(t).multiply(acc[i] / 2)).add(startpos[i]);
		}
		return ret;
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
	}
}
