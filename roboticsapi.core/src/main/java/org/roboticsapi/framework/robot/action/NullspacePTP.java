/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.action;

import java.util.Map;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.framework.cartesianmotion.action.CartesianMotionPlan;
import org.roboticsapi.framework.cartesianmotion.action.PathMotion;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.multijoint.action.JointMotion;
import org.roboticsapi.framework.multijoint.action.JointMotionPlan;
import org.roboticsapi.framework.multijoint.action.PTPPlan;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.framework.robot.RobotArm;
import org.roboticsapi.framework.robot.plan.NullspacePlan;

/**
 * Nullspace point to point motion following a given Cartesian path
 */
public class NullspacePTP extends JointMotion<JointMotionPlan> {
	/** joint positions (start / destination) */
	private final double[] from;
	private final double[] to;
	private final PathMotion<?> path;
	private final RobotArm robot;

	/**
	 * Creates a motion that follows a given Cartesian path and performs a null
	 * space motion from to the given start to the goal configuration
	 *
	 * @param from start position
	 * @param to   destination position
	 * @param path the Cartesian path to follow
	 */
	public NullspacePTP(final RobotArm robot, final double[] from, final double[] to, PathMotion<?> path) {
		super(0);
		this.robot = robot;
		this.from = copyArray(from);
		this.to = copyArray(to);
		this.path = path;
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

	public PathMotion<?> getPath() {
		return path;
	}

	public RobotArm getRobotArm() {
		return robot;
	}

	@Override
	public boolean supportsState(ActionRealtimeBoolean event) {
		return super.supportsState(event) || path.supportsState(event);
	}

	@Override
	public String toString() {
		return super.toString() + "<" + path + ">";
	}

	@Override
	public ActionRealtimeBoolean getCompletedState(Command scope) {
		return path.getCompletedState(scope);
	}

	@Override
	public ActionRealtimeBoolean getMotionTimeProgress(Command command, float progress) {
		return path.getMotionTimeProgress(command, progress);
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {

		final double[] from = getFrom(), to = getTo();
		final PathMotion<?> path = getPath();

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

		path.calculatePlan(plans, parameters);
		CartesianMotionPlan plan = (CartesianMotionPlan) plans.get(path);
		double duration = plan.getTotalTime();
		JointMotionPlan nullspacePlan = new PTPPlan(from, to, duration / 2, 0);

		plans.put(this, new NullspacePlan(plan, nullspacePlan, getRobotArm(),
				parameters.get(MotionCenterParameter.class).getMotionCenter()));

	}
}
