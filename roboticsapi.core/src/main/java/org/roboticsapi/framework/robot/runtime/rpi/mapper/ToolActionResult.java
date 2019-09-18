/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.mapper;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

public class ToolActionResult extends ActionResult {

	private final RealtimePose centerOfMass;
	private final RealtimeVector momentsOfInertia;
	private final RealtimeDouble mass;

	public ToolActionResult(Action action, RealtimeBoolean completion, RealtimePose centerOfMass,
			RealtimeVector momentsOfInertia, RealtimeDouble mass) {
		super(action, completion);

		this.centerOfMass = centerOfMass;
		this.momentsOfInertia = momentsOfInertia;
		this.mass = mass;
	}

	public RealtimePose getCenterOfMass() {
		return centerOfMass;
	}

	public RealtimeVector getMomentsOfInertia() {
		return momentsOfInertia;
	}

	public RealtimeDouble getMass() {
		return mass;
	}
}
