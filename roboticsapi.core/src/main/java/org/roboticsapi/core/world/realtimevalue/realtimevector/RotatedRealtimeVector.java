/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;

public class RotatedRealtimeVector extends RealtimeVector {

	private final RealtimeRotation rotation;
	private final RealtimeVector vector;

	RotatedRealtimeVector(RealtimeRotation rotation, RealtimeVector vector) {
		super(rotation, vector);
		this.rotation = rotation;
		this.vector = vector;

	}

	public RealtimeRotation getRotation() {
		return rotation;
	}

	public RealtimeVector getVector() {
		return vector;
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(rotation, vector);
	}

	@Override
	public String toString() {
		return "rotate(R: " + rotation + ", " + vector + ")";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RotatedRealtimeVector && rotation.equals(((RotatedRealtimeVector) obj).rotation)
				&& vector.equals(((RotatedRealtimeVector) obj).vector);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, rotation), vector);
	}

	@Override
	protected Vector calculateCheapValue() {
		Rotation rot = rotation.getCheapValue();
		Vector vec = vector.getCheapValue();
		return (rot == null || vec == null) ? null : rot.apply(vec);
	}

}
