/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.ActivityProperty;
import org.roboticsapi.world.Frame;

public class FrameGoalProperty implements ActivityProperty {
	private Frame goal;
	private Frame motionCenter;

	public FrameGoalProperty(Frame goal, Frame motionCenter) {
		this.goal = goal;
		this.motionCenter = motionCenter;
	}

	public Frame getMotionCenter() {
		return motionCenter;
	}

	public void setMotionCenter(Frame motionCenter) {
		this.motionCenter = motionCenter;
	}

	public Frame getGoal() {
		return goal;
	}

	public void setGoal(Frame goal) {
		this.goal = goal;
	}
}
