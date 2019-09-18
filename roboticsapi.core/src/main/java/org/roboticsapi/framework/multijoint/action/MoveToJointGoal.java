/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class MoveToJointGoal extends Action implements GoalAction, JointSpaceAction {

	private final RealtimeDouble[] goal;

	/**
	 * Instantiates a new MoveToCartesianGoal Action.
	 *
	 * @param goal the position to move to
	 */
	public MoveToJointGoal(final RealtimeDouble[] goal) {
		super(0, true, true);
		this.goal = goal;
	}

	/**
	 * Gets the position this Action moves to.
	 *
	 * @return the position
	 */
	public RealtimeDouble[] getGoal() {
		return goal;
	}

}
