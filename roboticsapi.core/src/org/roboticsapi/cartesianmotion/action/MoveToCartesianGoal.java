/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.world.Frame;

public class MoveToCartesianGoal extends Action implements GoalAction, CartesianPositionAction {

	private final Frame position;

	/**
	 * Instantiates a new MoveToCartesianGoal Action.
	 *
	 * @param position the position to move to
	 */
	public MoveToCartesianGoal(final Frame position) {
		super(0);
		this.position = position;
	}

	/**
	 * Gets the position this Action holds.
	 *
	 * @return the position
	 */
	public Frame getPosition() {
		return position;
	}

	@Override
	public List<ActionRealtimeException> defineActionExceptions() {
		List<ActionRealtimeException> exceptions = super.defineActionExceptions();
		exceptions.add(new ActionCancelledException(this));

		return exceptions;
	}
}
