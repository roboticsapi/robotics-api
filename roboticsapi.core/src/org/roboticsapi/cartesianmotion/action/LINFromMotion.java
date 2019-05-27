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
import org.roboticsapi.cartesianmotion.plan.LINFromMotionPlan;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

public class LINFromMotion extends PathMotion<LINFromMotionPlan> {
	private final Frame realFrom;
	private final Twist realFromVel;
	private final double speedFactor;

	public LINFromMotion(final Frame from, final Frame to, Frame realFrom, Twist realFromVel) {
		this(1, from, to, realFrom, realFromVel);
	}

	public LINFromMotion(double speedFactor, final Frame from, final Frame to, Frame realFrom, Twist realFromVel) {
		super(0);

		if (speedFactor <= 0 || speedFactor > 1) {
			throw new IllegalArgumentException("speedFactor must be greater than 0 and less or equal to 1");
		}
		this.speedFactor = speedFactor;
		this.from = from;
		this.to = to;
		this.realFrom = realFrom;
		this.realFromVel = realFromVel;
	}

	public double getSpeedFactor() {
		return speedFactor;
	}

	public void setFrom(Frame from) {
		this.from = from;
	}

	public Frame getRealFrom() {
		return realFrom;
	}

	public Twist getRealFromVel() {
		return realFromVel;
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {

		final double[] from = new double[3], to = new double[3];

		final Frame base = getFrom();
		Transformation f = null, t = null, rf = null;
		Twist rv = null;
		f = base.getTransformationTo(getFrom());
		t = base.getTransformationTo(getTo(), false);
		rf = base.getTransformationTo(getRealFrom());
		rv = getRealFromVel().changeOrientation(getRealFrom().getTransformationTo(base).getRotation());

		if (t == null) {
			throw new RoboticsException("Unreachable destination point (not statically connected to start point).");
		}

		from[0] = f.getTranslation().getX();
		from[1] = f.getTranslation().getY();
		from[2] = f.getTranslation().getZ();
		to[0] = t.getTranslation().getX();
		to[1] = t.getTranslation().getY();
		to[2] = t.getTranslation().getZ();

		final double angle = t.getRotation().getAngle();

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

		// calculate time for bezier fragment
		double realAccelTime = maxAccelTime;

		double consVel = t.getTranslation().getLength() / (maxAccelTime + maxConstantTime);
		double accelAcc = consVel / maxAccelTime;
		Vector accelEnd = t.getTranslation().normalize().scale(accelAcc * maxAccelTime * maxAccelTime / 2);

		double realCons = accelEnd.sub(rf.getTranslation()).getLength() / p.getMaximumPositionVelocity() * 1.5;
		if (realAccelTime < realCons) {
			realAccelTime = realCons;
		}

		double consRVel = t.getRotation().getAngle() / (maxAccelTime + maxConstantTime);
		double accelRAcc = consRVel / maxAccelTime;
		Rotation accelREnd = new Rotation(t.getRotation().getAxis(), accelRAcc * maxAccelTime * maxAccelTime / 2);

		double realRCons = accelREnd.multiply(rf.getRotation().invert()).getAngle() / p.getMaximumRotationVelocity()
				* 1.5;
		if (realAccelTime < realRCons) {
			realAccelTime = realRCons;
		}

		plans.put(this,
				new LINFromMotionPlan(base, f, t, rf, rv, mp.getMotionCenter(), parameters.get(OverrideParameter.class),
						maxAccelTime / getSpeedFactor(), maxConstantTime / getSpeedFactor(),
						realAccelTime / getSpeedFactor()));
	}

}
