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

public class JointConditionalPlan implements ExecutableJointMotionPlan {

	private final ExecutableJointMotionPlan main;
	private final ExecutableJointMotionPlan other;
	private final double start;
	private final double duration;

	public JointConditionalPlan(ExecutableJointMotionPlan main, ExecutableJointMotionPlan other, double start,
			double duration) {
		this.main = main;
		this.other = other;
		this.start = start;
		this.duration = duration;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getJointPositionsAt(time);
		} else {
			return other.getJointPositionsAt(time);
		}
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getJointVelocitiesAt(time);
		} else {
			return other.getJointVelocitiesAt(time);
		}
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getJointAccelerationsAt(time);
		} else {
			return other.getJointAccelerationsAt(time);
		}
	}

	@Override
	public double getTotalTime() {
		return 0;
	}

	@Override
	public Double getTimeAtFirstOccurence(State state) {
		Double mainOccurance = main.getTimeAtFirstOccurence(state);
		Double otherOccurance = other.getTimeAtFirstOccurence(state);
		if (otherOccurance < start) {
			return otherOccurance;
		}
		if (mainOccurance > start && mainOccurance < start + duration) {
			return mainOccurance;
		}
		return otherOccurance;
	}

	@Override
	public DoubleSensor[] getJointPositionSensorAt(DoubleSensor time) {
		DoubleSensor[] mainSensor = main.getJointPositionSensorAt(time);
		DoubleSensor[] otherSensor = other.getJointPositionSensorAt(time);
		DoubleSensor[] ret = new DoubleSensor[mainSensor.length];
		BooleanSensor cond = time.less(start + duration).and(time.less(start).not());
		for (int i = 0; i < mainSensor.length; i++) {
			ret[i] = DoubleSensor.conditional(cond, mainSensor[i], otherSensor[i]);
		}
		return ret;
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		BooleanSensor mainActive = time.less(start + duration).and(time.less(start).not());
		return mainActive.and(main.getStateSensorAt(time, state))
				.or(mainActive.not().and(other.getStateSensorAt(time, state)));
	}
}
