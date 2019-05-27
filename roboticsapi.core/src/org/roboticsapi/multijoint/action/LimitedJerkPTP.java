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
public class LimitedJerkPTP extends JointMotion<LimitedJerkPTPPlan> {
	/** joint positions (start / destination) */
	private final double[] from;
	private final double[] to;
	private final double speedFactor;

	public static LimitedJerkPTP to(final double[] from, final double[] to) {
		return new LimitedJerkPTP(from, to);
	}

	public static LimitedJerkPTP to(double speedFactor, final double[] from, final double[] to) {
		return new LimitedJerkPTP(speedFactor, from, to);
	}

	/**
	 * Creates a new point to point motion
	 *
	 * @param from start position
	 * @param to   destination position
	 */
	public LimitedJerkPTP(final double[] from, final double[] to) {
		this(1, from, to);
	}

	public LimitedJerkPTP(double speedFactor, final double[] from, final double[] to) {
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
		double maxConstJerkTime = 0, maxConstAccelTime = 0, maxConstVelTime = 0;
		for (int i = 0; i < p.getJointCount(); i++) {
			final JointParameters j = p.getJointParameters(i);
			if (to[i] > j.getMaximumPosition() || to[i] < j.getMinimumPosition()) {
				throw new RoboticsException("Invalid end position (J" + i + ": " + to[i] + " not in range ["
						+ j.getMinimumPosition() + ", " + j.getMaximumPosition() + "])");
			}

			if (Double.isInfinite(j.getMaximumJerk())) {
				throw new RoboticsException("JointParameters do not contain valid jerk limits for J" + i);
			}

			double Tj, Ta, Tv;

			// is maximum acceleration reached?
			if (j.getMaximumVelocity() * j.getMaximumJerk() > j.getMaximumAcceleration() * j.getMaximumAcceleration()) {
				Tj = j.getMaximumAcceleration() / j.getMaximumJerk();
				Ta = Tj + j.getMaximumVelocity() / j.getMaximumAcceleration();
			} else {
				Tj = Math.sqrt(j.getMaximumVelocity() / j.getMaximumJerk());
				Ta = 2 * Tj;
			}

			Tv = (Math.abs(to[i] - from[i]) / j.getMaximumVelocity()) - Ta;

			// no constant velocity phase
			if (Tv <= 0) {
				Tv = 0;

				// maximum acceleration reached?
				if (Math.abs(to[i] - from[i]) >= 2 * Math.pow(j.getMaximumAcceleration(), 3)
						/ Math.pow(j.getMaximumJerk(), 2)) {
					Tj = j.getMaximumAcceleration() / j.getMaximumJerk();
					Ta = Tj / 2
							+ Math.sqrt(Math.pow(Tj / 2, 2) + Math.abs(to[i] - from[i]) / j.getMaximumAcceleration());
				} else {
					Tj = Math.pow(Math.abs(to[i] - from[i]) / (2 * j.getMaximumJerk()), 1.0 / 3.0);
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

		plans.put(this, new LimitedJerkPTPPlan(from, to, maxConstJerkTime / getSpeedFactor(),
				maxConstAccelTime / getSpeedFactor(), maxConstVelTime / getSpeedFactor()));

	}
}
