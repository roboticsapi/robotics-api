/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.plan;

import org.roboticsapi.cartesianmotion.action.CartesianBezierSpline.AtPoint;
import org.roboticsapi.cartesianmotion.action.ExecutableCartesianMotionPlan;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.State;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.util.StateOccurenceTimeComputer;
import org.roboticsapi.core.util.TimeIntervals;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.TransformationSensor;

public class CartesianBezierSplinePlan implements ExecutableCartesianMotionPlan {

	private final ExecutableCartesianMotionPlan plan;
	private final double[] times;
	double duration;

	public CartesianBezierSplinePlan(ExecutableCartesianMotionPlan plan, double[] times) {
		this.plan = plan;
		this.times = times;
		duration = plan.getTotalTime();
	}

	@Override
	public Transformation getTransformationAt(double time) {
		return plan.getTransformationAt(time);
	}

	@Override
	public Twist getTwistAt(double time) {
		return plan.getTwistAt(time);
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
					ret.addInterval(duration * ((PlannedAction.TimeProgressState) state).getProgress(), duration);
					return ret;
				} else if (state instanceof AtPoint) {
					double timePercent = times[((AtPoint) state).getPoint()] / times[times.length - 1];
					return getCustomStateIntervals(new PlannedAction.TimeProgressState(timePercent));
				}
				return null;
			}
		}.getTimeAtFirstOccurence(state);
	}

	@Override
	public Frame getBaseFrame() {
		return plan.getBaseFrame();
	}

	@Override
	public TransformationSensor getTransformationSensorAt(DoubleSensor time) {
		return plan.getTransformationSensorAt(time);
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		if (state instanceof Action.CompletedState) {
			return time.greater(duration);
		} else if (state instanceof PlannedAction.TimeProgressState) {
			return time.less(duration * ((PlannedAction.TimeProgressState) state).getProgress()).not();
		} else if (state instanceof AtPoint) {
			double timePercent = times[((AtPoint) state).getPoint()] / times[times.length - 1];
			return getStateSensorAt(time, new PlannedAction.TimeProgressState(timePercent));
		}
		return null;
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		// TODO implement for this plan
		return null;
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		// TODO implement for this plan
		return null;
	}

}
