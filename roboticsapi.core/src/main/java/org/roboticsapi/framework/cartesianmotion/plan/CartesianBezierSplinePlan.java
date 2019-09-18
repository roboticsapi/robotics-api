/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.plan;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.util.RealtimeBooleanOccurenceTimeComputer;
import org.roboticsapi.core.util.TimeIntervals;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.framework.cartesianmotion.action.CartesianBezierSpline.AtPointRealtimeBoolean;
import org.roboticsapi.framework.cartesianmotion.action.ExecutableCartesianMotionPlan;

public class CartesianBezierSplinePlan implements ExecutableCartesianMotionPlan {

	private final ExecutableCartesianMotionPlan plan;
	private final double[] times;
	double duration;

	public CartesianBezierSplinePlan(ExecutableCartesianMotionPlan plan, double[] times) {
		this.plan = plan;
		this.times = times;
		duration = times[times.length - 1];
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
	public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
		return new RealtimeBooleanOccurenceTimeComputer() {
			@Override
			protected TimeIntervals getCustomStateIntervals(RealtimeBoolean state) {
				if (state instanceof Action.CompletedRealtimeBoolean) {
					TimeIntervals ret = new TimeIntervals();
					ret.addInterval(duration, duration);
					return ret;
				} else if (state instanceof PlannedAction.TimeProgressRealtimeBoolean) {
					TimeIntervals ret = new TimeIntervals();
					ret.addInterval(duration * ((PlannedAction.TimeProgressRealtimeBoolean) state).getProgress(),
							duration);
					return ret;
				} else if (state instanceof AtPointRealtimeBoolean) {
					double timePercent = times[((AtPointRealtimeBoolean) state).getPoint()] / times[times.length - 1];
					return getCustomStateIntervals(new PlannedAction.TimeProgressRealtimeBoolean(state.getScope(),
							((AtPointRealtimeBoolean) state).getAction(), timePercent));
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
	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time) {
		return plan.getTransformationSensorAt(time);
	}

	@Override
	public RealtimeTwist getTwistSensorAt(RealtimeDouble time) {
		return plan.getTwistSensorAt(time);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		if (state instanceof Action.CompletedRealtimeBoolean) {
			return time.greater(duration);
		} else if (state instanceof PlannedAction.TimeProgressRealtimeBoolean) {
			return time.less(duration * ((PlannedAction.TimeProgressRealtimeBoolean) state).getProgress()).not();
		} else if (state instanceof AtPointRealtimeBoolean) {
			double timePercent = times[((AtPointRealtimeBoolean) state).getPoint()] / times[times.length - 1];
			return getStateSensorAt(time, new PlannedAction.TimeProgressRealtimeBoolean(state.getScope(),
					((AtPointRealtimeBoolean) state).getAction(), timePercent));
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
