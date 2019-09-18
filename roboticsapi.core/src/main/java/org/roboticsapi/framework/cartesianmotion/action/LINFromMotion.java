/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import java.util.Map;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.Velocity;
import org.roboticsapi.core.world.World;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.plan.LINFromMotionPlan;

public class LINFromMotion extends PathMotion<LINFromMotionPlan> {
	private final Pose realFrom;
	private final Velocity realFromVel;
	private final double speedFactor;

	public LINFromMotion(double speedFactor, Pose from, final Pose to, Pose realFrom, Velocity realFromVel) {
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

	public LINFromMotion(Pose from, final Pose to, Pose realFrom, Velocity realFromVel) {
		this(1, from, to, realFrom, realFromVel);
	}

	public double getSpeedFactor() {
		return speedFactor;
	}

	public Pose getRealFrom() {
		return realFrom;
	}

	public Velocity getRealFromVel() {
		return realFromVel;
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {

		final double[] from = new double[3], to = new double[3];

		final Frame base = getFrom().getReference();
		Transformation f = null, t = null, rf = null;
		Twist rv = null;
		f = getFrom().getTransformation();
		t = base.getTransformationTo(getTo().getReference(), World.getCommandedTopology().withoutDynamic())
				.multiply(getTo().getTransformation());
		rf = base.getTransformationTo(getRealFrom().getReference()).multiply(getRealFrom().getTransformation());
		rv = getRealFromVel().getTwistForRepresentation(base.asOrientation(), null);

		if (t == null) {
			throw new RoboticsException("Unreachable destination point (not statically connected to start point).");
		}

		from[0] = f.getTranslation().getX();
		from[1] = f.getTranslation().getY();
		from[2] = f.getTranslation().getZ();
		to[0] = t.getTranslation().getX();
		to[1] = t.getTranslation().getY();
		to[2] = t.getTranslation().getZ();

		final double angle = f.getRotation().invert().multiply(t.getRotation()).getAngle();

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

		double consVel = f.invert().multiply(t).getTranslation().getLength() / (maxAccelTime + maxConstantTime);
		double accelAcc = consVel / maxAccelTime;
		Vector accelEnd = f.invert().multiply(t).getTranslation().normalize()
				.scale(accelAcc * maxAccelTime * maxAccelTime / 2);

		double realCons = accelEnd.sub(f.invert().multiply(rf).getTranslation()).getLength()
				/ p.getMaximumPositionVelocity() * 1.5;
		if (realAccelTime < realCons) {
			realAccelTime = realCons;
		}

		double consRVel = f.invert().multiply(t).getRotation().getAngle() / (maxAccelTime + maxConstantTime);
		double accelRAcc = consRVel / maxAccelTime;
		Rotation accelREnd = new Rotation(f.invert().multiply(t).getRotation().getAxis(),
				accelRAcc * maxAccelTime * maxAccelTime / 2);

		double realRCons = rf.invert().multiply(f).getRotation().multiply(accelREnd).getAngle()
				/ p.getMaximumRotationVelocity() * 1.5;
		if (realAccelTime < realRCons) {
			realAccelTime = realRCons;
		}

		plans.put(this,
				new LINFromMotionPlan(base, f, t, rf, rv, mp.getMotionCenter(), parameters.get(OverrideParameter.class),
						maxAccelTime / getSpeedFactor(), maxConstantTime / getSpeedFactor(),
						realAccelTime / getSpeedFactor()));
	}

}
