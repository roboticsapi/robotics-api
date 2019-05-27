/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.State;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.multijoint.Joint;

/**
 * Point to point motion in joint space
 */
public class JointErrorCorrection extends JointMotion<JointMotionPlan> {
	/** joint positions (start / destination) */
	private final double[] start;
	private final List<Joint> joints;
	private final JointMotion<?> wrappedMotion;

	public JointErrorCorrection(JointMotion<?> wrappedMotion, final List<? extends Joint> joints,
			final double[] start) {
		super(0);
		this.wrappedMotion = wrappedMotion;
		this.joints = new ArrayList<Joint>(joints);
		this.start = copyArray(start);
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

	@Override
	public List<ActionRealtimeException> defineActionExceptions() {
		return wrappedMotion.defineActionExceptions();
	}

	public List<Joint> getJoints() {
		return joints;
	}

	public double[] getStart() {
		return start;
	}

	public JointMotion<?> getWrappedMotion() {
		return wrappedMotion;
	}

	@Override
	public String toString() {
		return super.toString() + "<" + wrappedMotion.toString() + ">";
	}

	@Override
	public boolean supportsState(ActionState event) {
		return super.supportsState(event) || getWrappedMotion().supportsState(event);
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {
		wrappedMotion.calculatePlan(plans, parameters);
		final JointMotionPlan plan = (JointMotionPlan) plans.get(wrappedMotion);
		plans.put(this, new JointMotionPlan() {

			@Override
			public double getTotalTime() {
				return plan.getTotalTime();
			}

			@Override
			public Double getTimeAtFirstOccurence(State state) {
				return plan.getTimeAtFirstOccurence(state);
			}

			@Override
			public double[] getJointVelocitiesAt(double time) {
				return plan.getJointVelocitiesAt(time);
			}

			@Override
			public double[] getJointPositionsAt(double time) {
				return plan.getJointPositionsAt(time);
			}

			@Override
			public double[] getJointAccelerationsAt(double time) {
				return plan.getJointAccelerationsAt(time);
			}
		});
	}
}
