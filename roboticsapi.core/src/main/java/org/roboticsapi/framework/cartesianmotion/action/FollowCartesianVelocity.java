/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;

/**
 * Manual jogging of robot in Cartesian space.
 */
public class FollowCartesianVelocity extends Action implements ProcessAction, CartesianVelocityAction {

	private final RealtimeVelocity velocity;

	public FollowCartesianVelocity(RealtimeVelocity velocity) {
		super(0, false, true);
		this.velocity = velocity;
	}

	public RealtimeVelocity getVelocity() {
		return velocity;
	}

}
