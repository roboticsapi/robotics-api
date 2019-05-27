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
	public Double getTimeAtFirstOccurence(State state) {
		return null;
	}

	@Override
	public DoubleSensor[] getJointPositionSensorAt(DoubleSensor time) {
		DoubleSensor[] ret = new DoubleSensor[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = DoubleSensor.fromValue(pos[i]);
		}
		return ret;
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		return BooleanSensor.fromValue(false);
	}
}
