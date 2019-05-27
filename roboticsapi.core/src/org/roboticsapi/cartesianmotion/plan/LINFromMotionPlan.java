/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.plan;

import org.roboticsapi.cartesianmotion.action.ExecutableCartesianMotionPlan;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.State;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.util.StateOccurenceTimeComputer;
import org.roboticsapi.core.util.TimeIntervals;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.TransformationSensor;

public class LINFromMotionPlan implements ExecutableCartesianMotionPlan {

	private final ExecutableCartesianMotionPlan completeMotion;
	private final double duration;
	private final Frame base;
	private final double startTime;

	public LINFromMotionPlan(Frame base, Transformation from, Transformation to, Transformation realStart,
			Twist realStartVel, Frame motionCenter, OverrideParameter ovp, double accelTime, double constantTime,
			double realAccelTime) {

		this.base = base;
		this.duration = constantTime + 2 * accelTime;
		startTime = accelTime - realAccelTime;

		Transformation delta = to.multiply(from.invert());
		Rotation rotDelta = delta.getRotation();
		Vector rotAxis = rotDelta.getAxis();

		Twist consVel;
		Twist accelAcc, decelAcc;
		Transformation consPos, decelPos;

		consVel = new Twist( //
				delta.getTranslation().scale(1 / (accelTime + constantTime)), //
				rotAxis.scale(rotDelta.getAngle() / (accelTime + constantTime)));
		accelAcc = new Twist( //
				consVel.getTransVel().scale(1 / accelTime), //
				consVel.getRotVel().scale(1 / accelTime));
		decelAcc = new Twist( //
				consVel.getTransVel().scale(-1 / accelTime), //
				consVel.getRotVel().scale(-1 / accelTime));
		consPos = new Transformation(
				from.getRotation()
						.multiply(new Rotation(accelAcc.getRotVel().normalize(),
								accelAcc.getRotVel().getLength() * accelTime * accelTime / 2)), //
				from.getTranslation().add(accelAcc.getTransVel().scale(accelTime * accelTime / 2)));
		decelPos = new Transformation(
				consPos.getRotation().multiply(
						new Rotation(consVel.getRotVel().normalize(), consVel.getRotVel().getLength() * constantTime)), //
				consPos.getTranslation().add(consVel.getTransVel().scale(constantTime)));

		CartesianBezierPlan accel = new CartesianBezierPlan(base, realStart, realStartVel, consPos, consVel, 0,
				realAccelTime);

		CartesianConstantVelocityPlan constant = new CartesianConstantVelocityPlan(base, consPos, consVel,
				realAccelTime);

		CartesianConstantAccelerationPlan decel = new CartesianConstantAccelerationPlan(base, decelPos, consVel,
				decelAcc, realAccelTime + constantTime);

		CartesianConstantPositionPlan hold = new CartesianConstantPositionPlan(base, to);

		CartesianConditionalPlan decelHold = new CartesianConditionalPlan(decel, hold, realAccelTime + constantTime,
				accelTime);
		CartesianConditionalPlan constantDecelHold = new CartesianConditionalPlan(constant, decelHold, realAccelTime,
				constantTime);
		completeMotion = new CartesianConditionalPlan(accel, constantDecelHold, 0, realAccelTime);

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
	public Frame getBaseFrame() {
		return base;
	}

	@Override
	public TransformationSensor getTransformationSensorAt(DoubleSensor time) {
		return completeMotion.getTransformationSensorAt(time);
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
