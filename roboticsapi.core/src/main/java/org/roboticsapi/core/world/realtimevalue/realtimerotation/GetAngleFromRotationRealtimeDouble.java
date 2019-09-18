/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.roboticsapi.core.realtimevalue.realtimedouble.UnaryFunctionRealtimeDouble;
import org.roboticsapi.core.world.Rotation;

public final class GetAngleFromRotationRealtimeDouble extends UnaryFunctionRealtimeDouble<Rotation> {

	GetAngleFromRotationRealtimeDouble(RealtimeRotation rotation) {
		super(rotation);
	}

	public RealtimeRotation getRotation() {
		return (RealtimeRotation) getInnerValue();
	}

	@Override
	public String toString() {
		return "angle(" + getRotation() + ")";
	}

	@Override
	protected Double computeCheapValue(Rotation value) {
		return value.getAngle();
	}
}
