/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.property;

import org.roboticsapi.core.activity.ActivityProperty;

public class CommandedVelocityProperty implements ActivityProperty {

	private final double[] vel;

	public CommandedVelocityProperty(double[] vel) {
		this.vel = vel;
	}

	public double[] getVelocity() {
		return vel;
	}

}
