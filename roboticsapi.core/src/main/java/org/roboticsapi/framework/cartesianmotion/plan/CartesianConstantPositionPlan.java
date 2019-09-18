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
	public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
		return null;
	}

	@Override
	public Frame getBaseFrame() {
		return baseFrame;
	}

	@Override
	public RealtimeTwist getTwistSensorAt(RealtimeDouble time) {
		return RealtimeTwist.createFromConstant(new Twist(0, 0, 0, 0, 0, 0));
	}

	@Override
	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time) {
		return RealtimeTransformation.createfromConstant(startpos);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
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
