/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import java.util.Map;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;

/**
 * Point to point motion in joint space
 */
public class PTP extends JointMotion<PTPPlan> {
	/** joint positions (start / destination) */
	private final double[] from;
	private final double[] to;
	private final double speedFactor;

	public static PTP to(final double[] from, final double[] to) {
		return new PTP(from, to);
	}

	public static PTP to(double speedFactor, final double[] from, final double[] to) {
		return new PTP(speedFactor, from, to);
	}

	/**
	 * Creates a new point to point motion
	 * 
	 * @param from start position
	 * @param to   destination position
	 */
	public PTP(final double[] from, final double[] to) {
		this(1, from, to);
	}

	public PTP(double speedFactor, final double[] from, final double[] to) {
		super(0);

		if (speedFactor <= 0 || speedFactor > 1) {
			throw new IllegalArgumentException("speedFactor must be greater than 0 and less or equal to 1");
		}
		this.speedFactor = speedFactor;
		this.from = copyArray(from);
		this.to = copyArray(to);
	}

	private double[] copyArray(double[] source) {
		if (source == null) {
			return null;
		}

		double[] target = new double[source.length];

		for (int i = 0; i < source.length; i++) {
			target[i] = source[i];
		}

		return target;
	}

	public double[] getFrom() {
		return from;
	}

	public double[] getTo() {
		return to;
	}

	public double getSpeedFactor() {
		return speedFactor;
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {

		final double[] from = getFrom(), to = getTo();

		// check validity of parameters and positions
		final JointDeviceParameters p = parameters.get(JointDeviceParameters.class);
		if (p == null) {
			throw new RoboticsException("No joint device parameters given");
		}

		if (from == null || from.length != p.getJointCount()) {
			throw new RoboticsException("Invalid start position (numAxes)");
		}
		if (to == null || to.length != p.getJointCount()) {
			throw new RoboticsException("Invalid destination position (numAxes)");
		}

		// calculate acceleration and constant time
		double maxAccelTime = 0, maxConstantTime = 0;
		for (int i = 0; i < p.getJointCount(); i++) {
			final JointParameters j = p.getJointParameters(i);
			if (to[i] > j.getMaximumPosition() || to[i] < j.getMinimumPosition()) {
				throw new RoboticsException("Invalid end position (J" + i + ": " + to[i] + " not in range ["
						+ j.getMinimumPosition() + ", " + j.getMaximumPosition() + "])");
			}
			double constantTime = Math.abs((to[i] - from[i])) / j.getMaximumVelocity();
			double accelTime = j.getMaximumVelocity() / j.getMaximumAcceleration();
			if (constantTime >= accelTime) {
				constantTime -= accelTime;
			} else {
				constantTime = 0;
				accelTime = Math.sqrt(Math.abs(to[i] - from[i]) / j.getMaximumAcceleration());
			}
			if (constantTime > maxConstantTime) {
				maxConstantTime = constantTime;
			}
			if (accelTime > maxAccelTime) {
				maxAccelTime = accelTime;
			}
		}

		plans.put(this, new PTPPlan(from, to, maxAccelTime / getSpeedFactor(), maxConstantTime / getSpeedFactor()));

	}
}
