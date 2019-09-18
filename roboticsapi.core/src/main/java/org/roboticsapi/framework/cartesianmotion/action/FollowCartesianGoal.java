/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;

public class FollowCartesianGoal extends Action implements GoalAction, CartesianPositionAction {

	private final RealtimePose goal;

	public FollowCartesianGoal(Frame reference, RealtimeTransformation sensor) throws InitializationException {
		this(RealtimePose.createFromTransformation(reference, sensor));
	}

	public FollowCartesianGoal(Frame goal) {
		this(goal.asRealtimePose());
	}

	public FollowCartesianGoal(RealtimePose goal) {
		super(0, false, true);
		this.goal = goal;
	}

	public RealtimePose getGoal() {
		return goal;
	}

}
