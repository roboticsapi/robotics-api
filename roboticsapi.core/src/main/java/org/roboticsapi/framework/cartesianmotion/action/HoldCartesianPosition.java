/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;

/**
 * The HoldCartesianPosition Action lets an Actuator hold a Cartesian position
 * given by a (possibly moving) Frame.
 */
public class HoldCartesianPosition extends Action implements ProcessAction, CartesianPositionAction {

	private final RealtimePose position;

	/**
	 * Instantiates a new HoldCartesianPosition Action.
	 *
	 * @param position the position to hold
	 */
	public HoldCartesianPosition(final RealtimePose position) {
		super(0, false, true);
		this.position = position;
	}

	/**
	 * Instantiates a new HoldCartesianPosition Action.
	 *
	 * @param position the position to hold
	 */
	public HoldCartesianPosition(final Frame position) {
		this(position.asRealtimePose());
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
