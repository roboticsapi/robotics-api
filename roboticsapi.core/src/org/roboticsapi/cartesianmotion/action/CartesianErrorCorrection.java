/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import java.util.Map;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.State;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;

/**
 * Point to point motion in joint space
 */
public class CartesianErrorCorrection extends PathMotion<CartesianMotionPlan> {
	/** joint positions (start / destination) */
	private final Transformation start;
	private final Frame base;
	private final Frame frame;
	private final PathMotion<?> wrappedMotion;

	public CartesianErrorCorrection(PathMotion<?> wrappedMotion, final Frame frame, final Frame base,
			final Transformation start) {
		super(0);
		this.wrappedMotion = wrappedMotion;
		this.frame = frame;
		this.base = base;
		this.start = start;
	}

	public Transformation getStart() {
		return start;
	}

	public Frame getFrame() {
		return frame;
	}

	public Frame getBase() {
		return base;
	}

	public PathMotion<?> getWrappedMotion() {
		return wrappedMotion;
	}

	@Override
	public String toString() {
		return super.toString() + "<" + wrappedMotion.toString() + ">";
	}

	@Override
	public boolean supportsState(ActionState event) {
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
			public Double getTimeAtFirstOccurence(State state) {
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
