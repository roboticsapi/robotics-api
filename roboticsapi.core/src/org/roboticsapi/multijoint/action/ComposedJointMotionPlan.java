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

public class ComposedJointMotionPlan implements ExecutableJointMotionPlan {

	private final ExecutableJointMotionPlan firstPlan, secondPlan;
	private final State blendingState;

	private final double totalTime;
	private final double offset;

	public ComposedJointMotionPlan(ExecutableJointMotionPlan firstPlan, ExecutableJointMotionPlan secondPlan,
			State blendingState) {
		this.firstPlan = firstPlan;
		this.secondPlan = secondPlan;
		this.blendingState = blendingState;

		this.offset = computeOffset();
		this.totalTime = computeTotalTime();
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		ExecutableJointMotionPlan plan = getPlanFor(time);
		Double localTime = getLocalTimeFor(plan, time);

		return plan.getJointPositionsAt(localTime);
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		ExecutableJointMotionPlan plan = getPlanFor(time);
		Double localTime = getLocalTimeFor(plan, time);

		return plan.getJointVelocitiesAt(localTime);
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		ExecutableJointMotionPlan plan = getPlanFor(time);
		Double localTime = getLocalTimeFor(plan, time);

		return plan.getJointAccelerationsAt(localTime);
	}

	@Override
	public double getTotalTime() {
		return totalTime;
	}

	@Override
	public Double getTimeAtFirstOccurence(State state) {
		return new StateOccurenceTimeComputer() {
			@Override
			protected TimeIntervals getCustomStateIntervals(State state) {
				if (state instanceof Action.CompletedState) {
					TimeIntervals ret = new TimeIntervals();
					ret.addInterval(totalTime, totalTime);
					return ret;
				} else if (state instanceof PlannedAction.TimeProgressState) {
					TimeIntervals ret = new TimeIntervals();
					double progressReached = totalTime * ((PlannedAction.TimeProgressState) state).getProgress();
					ret.addInterval(progressReached, progressReached + 0.001);
					return ret;
				}
				return null;
			}
		}.getTimeAtFirstOccurence(state);
	}

	@Override
	public DoubleSensor[] getJointPositionSensorAt(DoubleSensor time) {
		DoubleSensor[] firstSensors = firstPlan.getJointPositionSensorAt(time);
		DoubleSensor[] secondSensors = secondPlan.getJointPositionSensorAt(time.add(-offset));

		DoubleSensor[] result = new DoubleSensor[firstSensors.length];
		BooleanSensor condition = time.less(offset);

		for (int i = 0; i < result.length; i++) {
			result[i] = DoubleSensor.conditional(condition, firstSensors[i], secondSensors[i]);
		}
		return result;
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		BooleanSensor condition = time.less(offset);

		return condition.and(firstPlan.getStateSensorAt(time, state))
				.or(condition.not().and(secondPlan.getStateSensorAt(time.add(-offset), state)));
	}

	private double getTotalTimeFor(ExecutableJointMotionPlan plan) {
		if (plan == firstPlan) {
			return offset;
		}
		return plan.getTotalTime();
	}

	private ExecutableJointMotionPlan getPlanFor(double time) {
		if (time < offset) {
			return firstPlan;
		}
		return secondPlan;
	}

	private double getLocalTimeFor(ExecutableJointMotionPlan plan, double time) {
		if (plan == firstPlan) {
			return time;
		}
		return time - offset;
	}

	private double computeTotalTime() {
		return getTotalTimeFor(firstPlan) + getTotalTimeFor(secondPlan);
	}

	private double computeOffset() {
		if (blendingState != null) {
			Double time = firstPlan.getTimeAtFirstOccurence(blendingState);

			if (time != null) {
				return time;
			}
		}
		return firstPlan.getTotalTime();
	}

}
