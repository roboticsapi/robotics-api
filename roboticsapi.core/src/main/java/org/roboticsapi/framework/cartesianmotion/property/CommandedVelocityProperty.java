/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.property;

import org.roboticsapi.core.activity.ActivityProperty;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Velocity;

public class CommandedVelocityProperty implements ActivityProperty {

	private final Velocity velocity;
	private final Pose motionCenter;

	/**
	 * @param velocity the current velocity at the corresponding activity result
	 */
	public CommandedVelocityProperty(Velocity velocity, Pose motionCenter) {
		this.velocity = velocity;
		this.motionCenter = motionCenter;
	}

	public Velocity getVelocity() {
		return velocity;
	}

	public Pose getMotionCenter() {
		return motionCenter;
	}

}
