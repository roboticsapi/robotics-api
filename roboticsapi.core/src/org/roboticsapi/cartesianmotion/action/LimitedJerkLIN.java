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
import org.roboticsapi.cartesianmotion.plan.LimitedJerkLINPlan;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;

public class LimitedJerkLIN extends PathMotion<LINPlan> {

	private final double speedFactor;

	public LimitedJerkLIN(final Frame from, final Frame to) {
		this(1, from, to);
	}

	public LimitedJerkLIN(double speedFactor, final Frame from, final Frame to) {
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
		final double[] from = new double[4], to = new double[4];
		final double[] maxvel = new double[4], maxacc = new double[4], maxjerk = new double[4];

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
		from[3] = 0; // start angle for rotation
		to[0] = toTransformation.getTranslation().getX();
		to[1] = toTransformation.getTranslation().getY();
		to[2] = toTransformation.getTranslation().getZ();
		to[3] = toTransformation.getRotation().getAngle(); // angle for rotation

		// final double angle = toTransformation.getRotation().getAngle();

		// check validity of parameters and positions
		final CartesianParameters p = parameters.get(CartesianParameters.class);

		if (p == null) {
			throw new RoboticsException("No cartesian parameters given");
		}

		if (Double.isInfinite(p.getMaximumPositionJerk()) || Double.isInfinite(p.getMaximumRotationJerk())) {
			throw new RoboticsException("CartesianParameters do not contain valid jerk limits");
		}

		final MotionCenterParameter mp = parameters.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		maxvel[0] = maxvel[1] = maxvel[2] = p.getMaximumPositionVelocity();
		maxvel[3] = p.getMaximumRotationVelocity();
		maxacc[0] = maxacc[1] = maxacc[2] = p.getMaximumPositionAcceleration();
		maxacc[3] = p.getMaximumRotationAcceleration();
		maxjerk[0] = maxjerk[1] = maxjerk[2] = p.getMaximumPositionJerk();
		maxjerk[3] = p.getMaximumRotationJerk();

		// calculate acceleration and constant time
		// see Biagiotti pp. 92
		double maxConstJerkTime = 0, maxConstAccelTime = 0, maxConstVelTime = 0;
		for (int i = 0; i < 4; i++) {
			double Tj, Ta, Tv;

			// is maximum acceleration reached?
			if (maxvel[i] * maxjerk[i] > maxacc[i] * maxacc[i]) {
				Tj = maxacc[i] / maxjerk[i];
				Ta = Tj + maxvel[i] / maxacc[i];
			} else {
				Tj = Math.sqrt(maxvel[i] / maxjerk[i]);
				Ta = 2 * Tj;
			}

			Tv = (Math.abs(to[i] - from[i]) / maxvel[i]) - Ta;

			// no constant velocity phase
			if (Tv <= 0) {
				Tv = 0;

				// maximum acceleration reached?
				if (Math.abs(to[i] - from[i]) >= 2 * Math.pow(maxacc[i], 3) / Math.pow(maxjerk[i], 2)) {
					Tj = maxacc[i] / maxjerk[i];
					Ta = Tj / 2 + Math.sqrt(Math.pow(Tj / 2, 2) + Math.abs(to[i] - from[i]) / maxacc[i]);
				} else {
					Tj = Math.pow(Math.abs(to[i] - from[i]) / (2 * maxjerk[i]), 1.0 / 3.0);
					Ta = 2 * Tj;
				}
			}

			if (Tj > maxConstJerkTime) {
				maxConstJerkTime = Tj;
			}
			if (Ta - 2 * Tj > maxConstAccelTime) {
				maxConstAccelTime = Ta - 2 * Tj;
			}
			if (Tv > maxConstVelTime) {
				maxConstVelTime = Tv;
			}
		}

		plans.put(this,
				new LimitedJerkLINPlan(base, fromTransformation, toTransformation, mp.getMotionCenter(),
						maxConstJerkTime / getSpeedFactor(), maxConstAccelTime / getSpeedFactor(),
						maxConstVelTime / getSpeedFactor()));

	}
}
