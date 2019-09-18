/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

public class CartesianGoalActionResult extends ActionResult {

	private final RealtimePose goal;
	private final RealtimeVelocity velocity;

	public CartesianGoalActionResult(Action action, RealtimeBoolean completion, RealtimePose goal) {
		super(action, completion);
		this.goal = goal;
		this.velocity = RealtimeVelocity.createFromConstant(new Twist(), goal.getReference(), null,
				goal.getReference().asOrientation());
	}

	public CartesianGoalActionResult(Action action, RealtimeBoolean completion, RealtimePose goal,
			RealtimeVelocity velocity) {
		super(action, completion);
		this.goal = goal;
		this.velocity = velocity;
	}

	public RealtimePose getGoal() {
		return goal;
	}

	public RealtimeVelocity getGoalVelocity() {
		return velocity;
	}

}
