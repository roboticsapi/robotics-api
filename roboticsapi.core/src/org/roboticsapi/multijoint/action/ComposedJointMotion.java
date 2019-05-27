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

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.State;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;

public class ComposedJointMotion extends JointMotion<ComposedJointMotionPlan> {

	private final List<JointMotion<? extends ExecutableJointMotionPlan>> motions = new ArrayList<JointMotion<? extends ExecutableJointMotionPlan>>();
	private final List<State> blendingStates = new ArrayList<State>();

	public ComposedJointMotion(JointMotion<? extends ExecutableJointMotionPlan> firstMotion,
			JointMotion<? extends ExecutableJointMotionPlan> secondMotion) {
		this(firstMotion, secondMotion, null);
	}

	public ComposedJointMotion(JointMotion<? extends ExecutableJointMotionPlan> firstMotion,
			JointMotion<? extends ExecutableJointMotionPlan> secondMotion, State blendingState) {
		super(0);

		if (firstMotion == null) {
			throw new IllegalArgumentException("The specified first joint motion cannot be null!");
		}
		motions.add(firstMotion);
		addMotion(secondMotion, blendingState);
	}

	public synchronized void addMotion(JointMotion<? extends ExecutableJointMotionPlan> motion, State blendingState) {
		if (motion == null) {
			throw new IllegalArgumentException("A succeeding joint motion cannot be null!");
		}
		motions.add(motion);
		blendingStates.add(blendingState);
	}

	public void addMotion(JointMotion<? extends ExecutableJointMotionPlan> motion) {
		addMotion(motion, null);
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {
		JointMotion<? extends ExecutableJointMotionPlan> motion = motions.get(motions.size() - 1);
		ExecutableJointMotionPlan composedPlan = calculate(motion, plans, parameters);

		for (int i = motions.size() - 2; i <= 0; i--) {
			motion = motions.get(i);
			ExecutableJointMotionPlan plan = calculate(motion, plans, parameters);
			State blendingState = blendingStates.get(i);

			composedPlan = new ComposedJointMotionPlan(plan, composedPlan, blendingState);
		}
		plans.put(this, composedPlan);
	}

	private ExecutableJointMotionPlan calculate(JointMotion<? extends ExecutableJointMotionPlan> motion,
			Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters) throws RoboticsException {
		motion.calculatePlan(plans, parameters);
		return (ExecutableJointMotionPlan) plans.get(motion);
	}

}
