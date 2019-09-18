/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.plan;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.framework.cartesianmotion.action.ExecutableCartesianMotionPlan;

public class CartesianConditionalPlan implements ExecutableCartesianMotionPlan {

	private final ExecutableCartesianMotionPlan main;
	private final ExecutableCartesianMotionPlan other;
	private final double start;
	private final double duration;

	public CartesianConditionalPlan(ExecutableCartesianMotionPlan main, ExecutableCartesianMotionPlan other,
			double start, double duration) {
		this.main = main;
		this.other = other;
		this.start = start;
		this.duration = duration;
	}

	@Override
	public Transformation getTransformationAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getTransformationAt(time);
		} else {
			return other.getTransformationAt(time);
		}
	}

	@Override
	public Twist getTwistAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getTwistAt(time);
		} else {
			return other.getTwistAt(time);
		}
	}

	@Override
	public double getTotalTime() {
		return 0;
	}

	@Override
	public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
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
	public Frame getBaseFrame() {
		return main.getBaseFrame();
	}

	@Override
	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time) {
		RealtimeTransformation mainSensor = main.getTransformationSensorAt(time);
		RealtimeTransformation otherSensor = other.getTransformationSensorAt(time);
		RealtimeBoolean cond = time.less(start + duration).and(time.less(start).not());
		return RealtimeTransformation.createConditional(cond, mainSensor, otherSensor);
	}

	@Override
	public RealtimeTwist getTwistSensorAt(RealtimeDouble time) {
		RealtimeTwist mainSensor = main.getTwistSensorAt(time);
		RealtimeTwist otherSensor = other.getTwistSensorAt(time);
		RealtimeBoolean cond = time.less(start + duration).and(time.less(start).not());
		return RealtimeTwist.createConditional(cond, mainSensor, otherSensor);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		RealtimeBoolean mainActive = time.less(start + duration).and(time.less(start).not());
		return mainActive.and(main.getStateSensorAt(time, state))
				.or(mainActive.not().and(other.getStateSensorAt(time, state)));
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		Double mainTime = main.getTimeForTransformation(t);
		if (mainTime != null && mainTime >= start && mainTime < start + duration) {
			return mainTime;
		} else {
			return other.getTimeForTransformation(t);
		}
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		Double mainTime = main.getTimeForTwist(t);
		if (mainTime != null && mainTime >= start && mainTime < start + duration) {
			return mainTime;
		} else {
			return other.getTimeForTwist(t);
		}
	}
}
