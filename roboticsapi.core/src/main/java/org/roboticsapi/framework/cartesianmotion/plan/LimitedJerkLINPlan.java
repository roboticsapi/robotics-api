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
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.framework.cartesianmotion.action.ExecutableCartesianMotionPlan;

public class LimitedJerkLINPlan implements ExecutableCartesianMotionPlan {

	private final Frame base;
	private final double duration;
	private final ExecutableCartesianMotionPlan completeMotion;

	public LimitedJerkLINPlan(Frame base, Transformation from, Transformation to, Pose motionCenter,
			double constJerkTime, double constAccelTime, double constVelTime) {
		this.base = base;
		this.duration = constVelTime + 2 * constAccelTime + 4 * constJerkTime;

		Transformation delta = to.multiply(from.invert());
		Rotation rotDelta = delta.getRotation();
		Vector rotAxis = rotDelta.getAxis();

		double Ta = constAccelTime + 2 * constJerkTime;
		double T = duration;
		double Tj = constJerkTime;

		double vmaxh = 1 / (T - Ta);
		double amaxh = 1 / ((T - Ta) * (Ta - Tj));
		double jmaxh = 1 / ((T - Ta) * (Ta - Tj) * Tj);

		Twist consVel = new Twist(//
				delta.getTranslation().scale(vmaxh), //
				rotAxis.scale(rotDelta.getAngle() * vmaxh));
		Twist consAcc = new Twist(delta.getTranslation().scale(amaxh), rotAxis.scale(rotDelta.getAngle() * amaxh));
		Twist consJerk = new Twist(delta.getTranslation().scale(jmaxh), rotAxis.scale(rotDelta.getAngle() * jmaxh));

		Twist consAccNeg = new Twist(delta.getTranslation().scale(-amaxh), rotAxis.scale(rotDelta.getAngle() * -amaxh));
		Twist consJerkNeg = new Twist(delta.getTranslation().scale(-jmaxh),
				rotAxis.scale(rotDelta.getAngle() * -jmaxh));

		Twist fromVel = new Twist(0, 0, 0, 0, 0, 0);
		Twist fromAcc = new Twist(0, 0, 0, 0, 0, 0);

		CartesianConstantJerkPlan con_jerk_acc1 = new CartesianConstantJerkPlan(base, from, fromVel, fromAcc, consJerk,
				0);

		CartesianConstantAccelerationPlan con_acc = new CartesianConstantAccelerationPlan(base,
				con_jerk_acc1.getTransformationAt(constJerkTime), con_jerk_acc1.getTwistAt(constJerkTime), consAcc,
				constJerkTime);

		double jerkacctime = constJerkTime + constAccelTime;
		CartesianConstantJerkPlan con_jerk_acc2 = new CartesianConstantJerkPlan(base,
				con_acc.getTransformationAt(jerkacctime), con_acc.getTwistAt(jerkacctime), consAcc, consJerkNeg,
				jerkacctime);

		double wholeAccelTime = 2 * constJerkTime + constAccelTime;

		CartesianConstantVelocityPlan con_vel = new CartesianConstantVelocityPlan(base,
				con_jerk_acc2.getTransformationAt(wholeAccelTime), consVel, wholeAccelTime);

		double wholeAccelConstTime = wholeAccelTime + constVelTime;

		CartesianConstantJerkPlan con_jerk_dec1 = new CartesianConstantJerkPlan(base,
				con_vel.getTransformationAt(wholeAccelConstTime), con_vel.getTwistAt(wholeAccelConstTime), fromAcc,
				consJerkNeg, wholeAccelConstTime);

		CartesianConstantAccelerationPlan con_dec = new CartesianConstantAccelerationPlan(base,
				con_jerk_dec1.getTransformationAt(wholeAccelConstTime + Tj),
				con_jerk_dec1.getTwistAt(wholeAccelConstTime + Tj), consAccNeg, wholeAccelConstTime + constJerkTime);

		CartesianConstantJerkPlan con_jerk_dec2 = new CartesianConstantJerkPlan(base,
				con_dec.getTransformationAt(wholeAccelConstTime + jerkacctime),
				con_dec.getTwistAt(wholeAccelConstTime + jerkacctime), consAccNeg, consJerk,
				wholeAccelConstTime + jerkacctime);

		CartesianConstantPositionPlan hold = new CartesianConstantPositionPlan(base, to);

		ExecutableCartesianMotionPlan p78 = new CartesianConditionalPlan(con_jerk_dec2, hold, T - Tj, Tj);
		ExecutableCartesianMotionPlan p678 = new CartesianConditionalPlan(con_dec, p78, T - Ta + Tj, Ta - 2 * Tj);
		ExecutableCartesianMotionPlan p5678 = new CartesianConditionalPlan(con_jerk_dec1, p678, T - Ta, Tj);
		ExecutableCartesianMotionPlan p45678 = new CartesianConditionalPlan(con_vel, p5678, Ta, T - 2 * Ta);
		ExecutableCartesianMotionPlan p345678 = new CartesianConditionalPlan(con_jerk_acc2, p45678, Ta - Tj, Tj);
		ExecutableCartesianMotionPlan p2345678 = new CartesianConditionalPlan(con_acc, p345678, Tj, Ta - 2 * Tj);
		completeMotion = new CartesianConditionalPlan(con_jerk_acc1, p2345678, 0, Tj);
	}

	@Override
	public Transformation getTransformationAt(double time) {
		return completeMotion.getTransformationAt(time);
	}

	@Override
	public Twist getTwistAt(double time) {
		return completeMotion.getTwistAt(time);
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
					double progressReached = duration
							* ((PlannedAction.TimeProgressRealtimeBoolean) state).getProgress();
					ret.addInterval(progressReached, progressReached + 0.001);
					return ret;
				}
				return null;
			}
		}.getTimeAtFirstOccurence(state);
	}

	@Override
	public Frame getBaseFrame() {
		return base;
	}

	@Override
	public RealtimeTwist getTwistSensorAt(RealtimeDouble time) {
		return completeMotion.getTwistSensorAt(time);
	}

	@Override
	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time) {
		return completeMotion.getTransformationSensorAt(time);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		if (state instanceof Action.CompletedRealtimeBoolean) {
			return time.greater(duration);
		} else if (state instanceof PlannedAction.TimeProgressRealtimeBoolean) {
			RealtimeBoolean progressReached = time
					.greater(duration * ((PlannedAction.TimeProgressRealtimeBoolean) state).getProgress());
			return progressReached.and(progressReached.fromHistory(0.01).not());
		}
		return null;
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		// TODO Auto-generated method stub
		return null;
	}

}
