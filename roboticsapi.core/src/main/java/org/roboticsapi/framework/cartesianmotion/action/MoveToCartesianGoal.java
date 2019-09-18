/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;

public class MoveToCartesianGoal extends Action implements GoalAction, CartesianPositionAction {

	private final RealtimePose position;

	/**
	 * Instantiates a new MoveToCartesianGoal Action.
	 *
	 * @param position the position to move to
	 */
	public MoveToCartesianGoal(final RealtimePose position) {
		super(0, true, true);
		this.position = position;
	}

	/**
	 * Gets the position this Action holds.
	 *
	 * @return the position
	 */
	public RealtimePose getPosition() {
		return position;
	}

}
