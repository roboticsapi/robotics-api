/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.world.Frame;

public class FollowFrame extends Action {

	private final Frame goal;

	public FollowFrame(Frame goal) {
		super(0);
		this.goal = goal;
	}

	public Frame getGoal() {
		return goal;
	}
}
