/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.plan;

import org.roboticsapi.core.State;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.action.ExecutableJointMotionPlan;

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
	public Double getTimeAtFirstOccurence(State state) {
		return null;
	}

	@Override
	public DoubleSensor[] getJointPositionSensorAt(DoubleSensor time) {
		DoubleSensor[] ret = new DoubleSensor[startpos.length];
		DoubleSensor t = time.add(-starttime);
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = t.multiply(startvel[i]).add(t.multiply(t).multiply(acc[i] / 2)).add(startpos[i]);
		}
		return ret;
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		return BooleanSensor.fromValue(false);
	}
}
