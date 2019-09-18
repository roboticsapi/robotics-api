/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class GetAxisFromRotationRealtimeVector extends RealtimeVector {

	private final RealtimeRotation rotation;

	GetAxisFromRotationRealtimeVector(RealtimeRotation rotation) {
		super(rotation);
		this.rotation = rotation;
	}

	public RealtimeRotation getRotation() {
		return rotation;
	}

	@Override
	protected Vector calculateCheapValue() {
		Rotation rotation = this.rotation.getCheapValue();

		if (rotation == null) {
			return null;
		}
		return rotation.getAxis();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && rotation.equals(((GetAxisFromRotationRealtimeVector) obj).rotation);
	}

	@Override
	public int hashCode() {
		return classHash(rotation);
	}

	@Override
	public boolean isAvailable() {
		return rotation.isAvailable();
	}

	@Override
	public String toString() {
		return "axis(" + rotation + ")";
	}
}
