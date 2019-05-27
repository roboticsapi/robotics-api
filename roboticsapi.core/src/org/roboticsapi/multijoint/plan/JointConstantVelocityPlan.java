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
	public Double getTimeAtFirstOccurence(State state) {
		return null;
	}

	@Override
	public DoubleSensor[] getJointPositionSensorAt(DoubleSensor time) {
		DoubleSensor[] ret = new DoubleSensor[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = time.add(-starttime).multiply(vel[i]).add(start[i]);
		}
		return ret;
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		return BooleanSensor.fromValue(false);
	}
}
