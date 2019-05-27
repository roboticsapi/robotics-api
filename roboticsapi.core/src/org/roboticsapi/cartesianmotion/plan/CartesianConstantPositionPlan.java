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

public class CartesianConstantPositionPlan implements ExecutableCartesianMotionPlan {

	private final Transformation startpos;
	private final Frame baseFrame;

	public CartesianConstantPositionPlan(Frame baseFrame, Transformation startpos) {
		this.baseFrame = baseFrame;
		this.startpos = startpos;
	}

	@Override
	public Transformation getTransformationAt(double time) {
		return startpos;
	}

	@Override
	public Twist getTwistAt(double time) {
		return new Twist(0, 0, 0, 0, 0, 0);
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
	public Frame getBaseFrame() {
		return baseFrame;
	}

	@Override
	public TransformationSensor getTransformationSensorAt(DoubleSensor time) {
		return TransformationSensor.fromConstant(startpos);
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		return BooleanSensor.fromValue(false);
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		if (startpos.isEqualTransformation(t)) {
			return 0d;
		} else {
			return null;
		}
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		if (t.isEqualTwist(new Twist(0, 0, 0, 0, 0, 0))) {
			return 0d;
		} else {
			return null;
		}
	}
}
