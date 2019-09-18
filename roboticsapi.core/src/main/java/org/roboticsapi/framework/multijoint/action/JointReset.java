/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.Action;

/**
 * A {@link JointReset} sets a new joint value which results in a position jump
 * of the joint. This of course is possible only on simulated actuators, real
 * actuators may go into faststop state when trying to execute this action.
 */
public final class JointReset extends Action {

	private final double newPosition;

	public JointReset(double newPosition) {
		super(0, true, false);
		this.newPosition = newPosition;
	}

	public final double getNewPosition() {
		return newPosition;
	}

}
