/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import java.util.Map;

import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.cartesianmotion.plan.LINPlan;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;

public class LIN extends PathMotion<LINPlan> {

	private final double speedFactor;

	public LIN(final Frame from, final Frame to) {
		this(1, from, to);
	}

	public LIN(double speedFactor, final Frame from, final Frame to) {
		super(0);

		if (speedFactor <= 0 || speedFactor > 1) {
			throw new IllegalArgumentException("speedFactor must be greater than 0 and less or equal to 1");
		}
		this.speedFactor = speedFactor;
		this.from = from;
		this.to = to;
	}

	public double getSpeedFactor() {
		return speedFactor;
	}

	public void setFrom(Frame from) {
		this.from = from;
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {
		final double[] from = new double[3], to = new double[3];

		final Frame base = getFrom();
		Transformation fromTransformation = null, toTransformation = null;
		fromTransformation = base.getTransformationTo(getFrom());
		toTransformation = base.getTransformationTo(getTo(), false);

		if (toTransformation == null) {
			throw new RoboticsException("Unreachable destination point (not statically connected to start point).");
		}

		from[0] = fromTransformation.getTranslation().getX();
		from[1] = fromTransformation.getTranslation().getY();
		from[2] = fromTransformation.getTranslation().getZ();
		to[0] = toTransformation.getTranslation().getX();
		to[1] = toTransformation.getTranslation().getY();
		to[2] = toTransformation.getTranslation().getZ();

		final double angle = toTransformation.getRotation().getAngle();

		// check validity of parameters and positions
		final CartesianParameters p = parameters.get(CartesianParameters.class);

		if (p == null) {
			throw new RoboticsException("No cartesian parameters given");
		}

		final MotionCenterParameter mp = parameters.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		// calculate acceleration and constant time
		double maxAccelTime = 0, maxConstantTime = 0;
		for (int i = 0; i < 3; i++) {
			double constantTime = Math.abs((to[i] - from[i])) / p.getMaximumPositionVelocity();
			double accelTime = p.getMaximumPositionVelocity() / p.getMaximumPositionAcceleration();
			if (constantTime >= accelTime) {
				constantTime -= accelTime;
			} else {
				constantTime = 0;
				accelTime = Math.sqrt(Math.abs(to[i] - from[i]) / p.getMaximumPositionAcceleration());
			}
			if (constantTime > maxConstantTime) {
				maxConstantTime = constantTime;
			}
			if (accelTime > maxAccelTime) {
				maxAccelTime = accelTime;
			}
		}

		double constantTime = Math.abs(angle) / p.getMaximumRotationVelocity();
		double accelTime = p.getMaximumRotationVelocity() / p.getMaximumRotationAcceleration();
		if (constantTime >= accelTime) {
			constantTime -= accelTime;
		} else {
			constantTime = 0;
			accelTime = Math.sqrt(Math.abs(angle) / p.getMaximumRotationAcceleration());
		}
		if (constantTime > maxConstantTime) {
			maxConstantTime = constantTime;
		}
		if (accelTime > maxAccelTime) {
			maxAccelTime = accelTime;
		}

		plans.put(this, new LINPlan(base, fromTransformation, toTransformation, mp.getMotionCenter(),
				maxAccelTime / getSpeedFactor(), maxConstantTime / getSpeedFactor()));

	}
}
