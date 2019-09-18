/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import java.util.Map;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;

/**
 * Point to point motion in joint space
 */
public class CartesianErrorCorrection extends PathMotion<CartesianMotionPlan> {
	/** joint positions (start / destination) */
	private final PathMotion<?> wrappedMotion;
	private final Pose startPose, motionCenter;
	private final FrameTopology topology;

	public CartesianErrorCorrection(PathMotion<?> wrappedMotion, final Pose startPose, final Pose motionCenter,
			final FrameTopology topology) {
		super(0);
		this.wrappedMotion = wrappedMotion;
		this.startPose = startPose;
		this.motionCenter = motionCenter;
		this.topology = topology;
	}

	@Override
	public ActionRealtimeBoolean getCompletedState(Command scope) {
		return wrappedMotion.getCompletedState(scope);
	}

	@Override
	public ActionRealtimeBoolean getMotionTimeProgress(Command command, float progress) {
		return wrappedMotion.getMotionTimeProgress(command, progress);
	}

	@Override
	public ActionRealtimeBoolean getMotionTimeProgress(Command command, Frame f, CartesianMotionPlan plan) {
		return wrappedMotion.getMotionTimeProgress(command, f, plan);
	}

	public Pose getStartPose() {
		return startPose;
	}

	public Pose getMotionCenter() {
		return motionCenter;
	}

	public FrameTopology getTopology() {
		return topology;
	}

	public PathMotion<?> getWrappedMotion() {
		return wrappedMotion;
	}

	@Override
	public String toString() {
		return super.toString() + "<" + wrappedMotion.toString() + ">";
	}

	@Override
	public boolean supportsState(ActionRealtimeBoolean event) {
		return super.supportsState(event) || wrappedMotion.supportsState(event);
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {
		wrappedMotion.calculatePlan(plans, parameters);
		final CartesianMotionPlan plan = (CartesianMotionPlan) plans.get(wrappedMotion);
		plans.put(this, new CartesianMotionPlan() {

			@Override
			public double getTotalTime() {
				return plan.getTotalTime();
			}

			@Override
			public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
				return plan.getTimeAtFirstOccurence(state);
			}

			@Override
			public Twist getTwistAt(double time) {
				return plan.getTwistAt(time);
			}

			@Override
			public Transformation getTransformationAt(double time) {
				return plan.getTransformationAt(time);
			}

			@Override
			public Frame getBaseFrame() {
				return plan.getBaseFrame();
			}

			@Override
			public Double getTimeForTransformation(Transformation t) {
				return plan.getTimeForTransformation(t);
			}

			@Override
			public Double getTimeForTwist(Twist t) {
				return plan.getTimeForTwist(t);
			}
		});
	}

	public Double getTimeForTwist(Twist t) {
		return null;
	}

	public Double getTimeForTransformation(Transformation t) {
		return null;
	}
}
