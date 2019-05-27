/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.State;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.util.StateOccurenceTimeComputer;
import org.roboticsapi.core.util.TimeIntervals;
import org.roboticsapi.multijoint.plan.JointBezierPlan;
import org.roboticsapi.multijoint.plan.JointConditionalPlan;
import org.roboticsapi.multijoint.plan.JointConstantAccelerationPlan;
import org.roboticsapi.multijoint.plan.JointConstantPositionPlan;
import org.roboticsapi.multijoint.plan.JointConstantVelocityPlan;

public class PTPFromMotionPlan implements ExecutableJointMotionPlan {

	private final ExecutableJointMotionPlan completeMotion;
	private final double duration;
	private final double startTime;

	public PTPFromMotionPlan(double[] from, double[] to, double[] realStart, double[] realStartVel, double accelTime,
			double constantTime, double realAccelTime) throws RoboticsException {

		this.duration = constantTime + 2 * accelTime;
		startTime = accelTime - realAccelTime;

		double[] fromVel = new double[from.length], consVel = new double[from.length];
		double[] accelAcc = new double[from.length], decelAcc = new double[from.length];
		double[] consPos = new double[from.length], decelPos = new double[from.length];
		for (int i = 0; i < from.length; i++) {
			fromVel[i] = 0;
			consVel[i] = (to[i] - from[i]) / (accelTime + constantTime);
			accelAcc[i] = consVel[i] / accelTime;
			decelAcc[i] = -consVel[i] / accelTime;
			consPos[i] = from[i] + accelAcc[i] * accelTime * accelTime / 2;
			decelPos[i] = consPos[i] + consVel[i] * constantTime;
		}

		JointBezierPlan accel = new JointBezierPlan(realStart, realStartVel, consPos, consVel, 0, realAccelTime);

		JointConstantVelocityPlan constant = new JointConstantVelocityPlan(consPos, consVel, realAccelTime);

		JointConstantAccelerationPlan decel = new JointConstantAccelerationPlan(decelPos, consVel, decelAcc,
				realAccelTime + constantTime);

		JointConstantPositionPlan hold = new JointConstantPositionPlan(to);

		ExecutableJointMotionPlan decelHold = new JointConditionalPlan(decel, hold, realAccelTime + constantTime,
				accelTime);
		ExecutableJointMotionPlan constantDecelHold = new JointConditionalPlan(constant, decelHold, realAccelTime,
				constantTime);
		completeMotion = new JointConditionalPlan(accel, constantDecelHold, 0, realAccelTime);
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		return completeMotion.getJointPositionsAt(time);
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		return completeMotion.getJointVelocitiesAt(time);
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		return completeMotion.getJointAccelerationsAt(time);
	}

	@Override
	public double getTotalTime() {
		return duration - startTime;
	}

	@Override
	public Double getTimeAtFirstOccurence(State state) {
		return new StateOccurenceTimeComputer() {
			@Override
			protected TimeIntervals getCustomStateIntervals(State state) {
				if (state instanceof Action.CompletedState) {
					TimeIntervals ret = new TimeIntervals();
					ret.addInterval(duration - startTime, duration - startTime);
					return ret;
				} else if (state instanceof PlannedAction.TimeProgressState) {
					TimeIntervals ret = new TimeIntervals();
					double progressReached = duration * ((PlannedAction.TimeProgressState) state).getProgress()
							- startTime;
					ret.addInterval(progressReached, progressReached + 0.001);
					return ret;
				}
				return null;
			}
		}.getTimeAtFirstOccurence(state);
	}

	@Override
	public DoubleSensor[] getJointPositionSensorAt(DoubleSensor time) {
		return completeMotion.getJointPositionSensorAt(time);
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		if (state instanceof Action.CompletedState) {
			return time.greater(duration - startTime);
		} else if (state instanceof PlannedAction.TimeProgressState) {
			BooleanSensor progressReached = time
					.greater(duration * ((PlannedAction.TimeProgressState) state).getProgress() - startTime);
			return progressReached.and(progressReached.fromHistory(0.01).not());
		}
		return null;
	}
}
