/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.framework.multijoint.Joint;

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

	@Override
	public ActionRealtimeBoolean getCompletedState(Command scope) {
		return wrappedMotion.getCompletedState(scope);
	}

	@Override
	public ActionRealtimeBoolean getMotionTimeProgress(Command command, float progress) {
		return wrappedMotion.getMotionTimeProgress(command, progress);
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
	public List<ActionRealtimeException> defineActionExceptions(Command scope) {
		return wrappedMotion.defineActionExceptions(scope);
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
	public boolean supportsState(ActionRealtimeBoolean event) {
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
			public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
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
