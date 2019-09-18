/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

public class CartesianVelocityActionResult extends ActionResult {

	private final RealtimeVelocity velocity;

	public CartesianVelocityActionResult(Action action, RealtimeBoolean completion, RealtimeVelocity velocity) {
		super(action, completion);
		this.velocity = velocity;
	}

	public RealtimeVelocity getVelocity() {
		return velocity;
	}

}
