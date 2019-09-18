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

public class JointBezierPlan implements ExecutableJointMotionPlan {

	private final double[] startpos;
	private final double[] startvel;
	private final double[] destpos;
	private final double[] destvel;
	private final double starttime;
	private final double duration;

	public JointBezierPlan(double[] startpos, double[] startvel, double[] destpos, double[] destvel, double starttime,
			double duration) {
		this.startpos = startpos;
		this.startvel = startvel;
		this.destpos = destpos;
		this.destvel = destvel;
		this.starttime = starttime;
		this.duration = duration;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		double[] ret = new double[startpos.length];
		double t = (time - starttime) / duration;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = calculateBezier(t, startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration);
		}
		return ret;
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		double[] ret = new double[startpos.length];
		double t = (time - starttime) / duration;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = calculateBezierVel(t, startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration)
					/ duration;
		}
		return ret;
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		double[] ret = new double[startpos.length];
		double t = (time - starttime) / duration;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = calculateBezierAcc(t, startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration)
					/ duration / duration;
		}
		return ret;
	}

	private double calculateBezier(double time, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double a = from;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		double t = time;
		return a + t * (b + t * (c + d * t));
	}

	private double calculateBezierVel(double time, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		double t = time;
		return b + t * (2 * c + 3 * d * t);
	}

	private double calculateBezierAcc(double time, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		double t = time;
		return 2 * c + 6 * d * t;
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
		RealtimeDouble t = time.add(-starttime).divide(duration);
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = calculateBezierSensor(t, startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration);
		}
		return ret;
	}

	private RealtimeDouble calculateBezierSensor(RealtimeDouble t, double from, double to, double fromvel,
			double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double a = from;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		return t.multiply(d).add(c).multiply(t).add(b).multiply(t).add(a);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
	}
}
