/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.plan;

import org.roboticsapi.cartesianmotion.action.ExecutableCartesianMotionPlan;
import org.roboticsapi.core.State;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.TransformationSensor;

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
	public Frame getBaseFrame() {
		return main.getBaseFrame();
	}

	@Override
	public TransformationSensor getTransformationSensorAt(DoubleSensor time) {
		TransformationSensor mainSensor = main.getTransformationSensorAt(time);
		TransformationSensor otherSensor = other.getTransformationSensorAt(time);
		BooleanSensor cond = time.less(start + duration).and(time.less(start).not());
		return TransformationSensor.conditional(cond, mainSensor, otherSensor);
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		BooleanSensor mainActive = time.less(start + duration).and(time.less(start).not());
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
