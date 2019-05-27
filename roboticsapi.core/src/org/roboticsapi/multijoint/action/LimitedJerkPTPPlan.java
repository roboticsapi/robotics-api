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
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.util.StateOccurenceTimeComputer;
import org.roboticsapi.core.util.TimeIntervals;
import org.roboticsapi.multijoint.plan.JointConditionalPlan;
import org.roboticsapi.multijoint.plan.JointConstantAccelerationPlan;
import org.roboticsapi.multijoint.plan.JointConstantJerkPlan;
import org.roboticsapi.multijoint.plan.JointConstantPositionPlan;
import org.roboticsapi.multijoint.plan.JointConstantVelocityPlan;

public class LimitedJerkPTPPlan implements ExecutableJointMotionPlan {

	private final ExecutableJointMotionPlan completeMotion;
	private final double duration;

	public LimitedJerkPTPPlan(double[] from, double[] to, double constJerkTime, double constAccelTime,
			double constVelTime) {
		this.duration = constVelTime + 2 * constAccelTime + 4 * constJerkTime;

		double Ta = constAccelTime + 2 * constJerkTime;
		double T = duration;
		double Tj = constJerkTime;

		double vmaxh = 1 / (T - Ta);
		double amaxh = 1 / ((T - Ta) * (Ta - Tj));
		double jmaxh = 1 / ((T - Ta) * (Ta - Tj) * Tj);

		double[] fromVel = new double[from.length], consVel = new double[from.length];
		double[] fromAcc = new double[from.length], consAcc = new double[from.length];
		double[] consJerk = new double[from.length];
		double[] consAccNeg = new double[from.length], consJerkNeg = new double[from.length];

		for (int i = 0; i < from.length; i++) {
			fromVel[i] = 0;
			fromAcc[i] = 0;
			consVel[i] = (to[i] - from[i]) * vmaxh;
			consAcc[i] = (to[i] - from[i]) * amaxh;
			consJerk[i] = (to[i] - from[i]) * jmaxh;
			consAccNeg[i] = -consAcc[i];
			consJerkNeg[i] = -consJerk[i];
		}

		JointConstantJerkPlan con_jerk_acc1 = new JointConstantJerkPlan(from, fromVel, fromAcc, consJerk, 0);

		JointConstantAccelerationPlan con_acc = new JointConstantAccelerationPlan(
				con_jerk_acc1.getJointPositionsAt(constJerkTime), con_jerk_acc1.getJointVelocitiesAt(constJerkTime),
				consAcc, constJerkTime);

		double jerkacctime = constJerkTime + constAccelTime;
		JointConstantJerkPlan con_jerk_acc2 = new JointConstantJerkPlan(con_acc.getJointPositionsAt(jerkacctime),
				con_acc.getJointVelocitiesAt(jerkacctime), consAcc, consJerkNeg, jerkacctime);

		double wholeAccelTime = 2 * constJerkTime + constAccelTime;

		JointConstantVelocityPlan con_vel = new JointConstantVelocityPlan(
				con_jerk_acc2.getJointPositionsAt(wholeAccelTime), consVel, wholeAccelTime);

		double wholeAccelConstTime = wholeAccelTime + constVelTime;

		JointConstantJerkPlan con_jerk_dec1 = new JointConstantJerkPlan(
				con_vel.getJointPositionsAt(wholeAccelConstTime), con_vel.getJointVelocitiesAt(wholeAccelConstTime),
				fromAcc, consJerkNeg, wholeAccelConstTime);

		JointConstantAccelerationPlan con_dec = new JointConstantAccelerationPlan(
				con_jerk_dec1.getJointPositionsAt(wholeAccelConstTime + Tj),
				con_jerk_dec1.getJointVelocitiesAt(wholeAccelConstTime + Tj), consAccNeg,
				wholeAccelConstTime + constJerkTime);

		JointConstantJerkPlan con_jerk_dec2 = new JointConstantJerkPlan(
				con_dec.getJointPositionsAt(wholeAccelConstTime + jerkacctime),
				con_dec.getJointVelocitiesAt(wholeAccelConstTime + jerkacctime), consAccNeg, consJerk,
				wholeAccelConstTime + jerkacctime);

		JointConstantPositionPlan hold = new JointConstantPositionPlan(to);

		ExecutableJointMotionPlan p78 = new JointConditionalPlan(con_jerk_dec2, hold, T - Tj, Tj);
		ExecutableJointMotionPlan p678 = new JointConditionalPlan(con_dec, p78, T - Ta + Tj, Ta - 2 * Tj);
		ExecutableJointMotionPlan p5678 = new JointConditionalPlan(con_jerk_dec1, p678, T - Ta, Tj);
		ExecutableJointMotionPlan p45678 = new JointConditionalPlan(con_vel, p5678, Ta, T - 2 * Ta);
		ExecutableJointMotionPlan p345678 = new JointConditionalPlan(con_jerk_acc2, p45678, Ta - Tj, Tj);
		ExecutableJointMotionPlan p2345678 = new JointConditionalPlan(con_acc, p345678, Tj, Ta - 2 * Tj);
		completeMotion = new JointConditionalPlan(con_jerk_acc1, p2345678, 0, Tj);
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
		return duration;
	}

	@Override
	public Double getTimeAtFirstOccurence(State state) {
		return new StateOccurenceTimeComputer() {
			@Override
			protected TimeIntervals getCustomStateIntervals(State state) {
				if (state instanceof Action.CompletedState) {
					TimeIntervals ret = new TimeIntervals();
					ret.addInterval(duration, duration);
					return ret;
				} else if (state instanceof PlannedAction.TimeProgressState) {
					TimeIntervals ret = new TimeIntervals();
					double progressReached = duration * ((PlannedAction.TimeProgressState) state).getProgress();
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
			return time.greater(duration);
		} else if (state instanceof PlannedAction.TimeProgressState) {
			BooleanSensor progressReached = time
					.greater(duration * ((PlannedAction.TimeProgressState) state).getProgress());
			return progressReached.and(progressReached.fromHistory(0.01).not());
		}
		return null;
	}
}
