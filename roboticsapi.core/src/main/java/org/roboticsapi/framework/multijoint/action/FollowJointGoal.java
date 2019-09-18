/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * The FollowJointGoal Action lets an Actuator follow the joint positions given
 * by a set of Sensors. An online planner is used to interpolate the movement.
 * The list of Sensors given is mapped to the actuator's joints in the order
 * they appear.
 */
public class FollowJointGoal extends Action implements GoalAction, JointSpaceAction {

	private final RealtimeDouble[] goal;

	/**
	 * Instantiates a new FollowJointGoal action.
	 *
	 * @param goal the sensors that determine the axis target positions.
	 */
	public FollowJointGoal(RealtimeDouble... goal) {
		super(0, false, true);
		if (goal == null) {
			throw new IllegalArgumentException("goal");
		}
		this.goal = goal;
	}

	/**
	 * Gets the sensors used in this FollowJointGoal.
	 *
	 * @return the sensors
	 */
	public RealtimeDouble[] getGoal() {
		return goal;
	}

	@Override
	public List<ActionRealtimeException> defineActionExceptions(Command scope) {
		List<ActionRealtimeException> exceptions = super.defineActionExceptions(scope);
		return exceptions;
	}

}
