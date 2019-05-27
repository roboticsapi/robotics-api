/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * Manual jogging of robot in Cartesian space.
 */
public class HoldCartesianVelocity extends Action implements ProcessAction, CartesianVelocityAction {

	private final VelocitySensor velocity;

	public HoldCartesianVelocity(VelocitySensor velocity) {
		super(0);

		this.velocity = velocity;

		if (velocity.getMovingFrame().getRelationsTo(velocity.getReferenceFrame()) == null) {
			throw new IllegalArgumentException("Moving Frame and Reference Frame must be connected somehow");
		}

		if (velocity.getMovingFrame().getRelationsTo(velocity.getPivotPoint().getReferenceFrame()) == null) {
			throw new IllegalArgumentException("Moving Frame and Pivot Point must be connected somehow");
		}

		if (velocity.getMovingFrame().getRelationsTo(velocity.getOrientation().getReferenceFrame()) == null) {
			throw new IllegalArgumentException("Moving Frame and Orientation must be connected somehow");
		}
	}

	public VelocitySensor getVelocity() {
		return velocity;
	}

}
