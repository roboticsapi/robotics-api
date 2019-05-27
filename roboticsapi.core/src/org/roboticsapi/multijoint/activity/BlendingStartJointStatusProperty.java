/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.ActivityProperty;

public class BlendingStartJointStatusProperty implements ActivityProperty {

	private final double[] pos;
	private final double[] vel;

	public BlendingStartJointStatusProperty(double[] pos, double[] vel) {
		this.pos = pos;
		this.vel = vel;
	}

	public double[] getVel() {
		return vel;
	}

	public double[] getPos() {
		return pos;
	}
}
