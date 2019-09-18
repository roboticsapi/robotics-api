/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

/**
 * Action result describing a Cartesian position
 */
public class CartesianPositionActionResult extends ActionResult {

	private final RealtimePose position;
	private RealtimeVelocity velocity;

	public CartesianPositionActionResult(Action action, RealtimeBoolean completion, RealtimePose position) {
		this(action, completion, position, null);
	}

	public CartesianPositionActionResult(Action action, RealtimeBoolean completion, RealtimePose position,
			RealtimeVelocity velocity) {
		super(action, completion);
		this.position = position;
		this.velocity = velocity;
	}

	public CartesianPositionActionResult(Action action, RealtimeBoolean completion, ActionResult innerResult,
			RealtimePose position) {
		super(action, completion, innerResult);
		this.position = position;
	}

	public RealtimePose getPosition() {
		return position;
	}

	public RealtimeVelocity getVelocity() {
		return velocity;
	}

}
