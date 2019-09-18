/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;

public final class OrientationAdaptedRealtimeTwist extends RealtimeTwist {

	@Override
	protected RealtimeTwist performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new OrientationAdaptedRealtimeTwist(other.substitute(substitutionMap),
				orientationChange.substitute(substitutionMap));
	}

	private final RealtimeRotation orientationChange;
	private final RealtimeTwist other;

	OrientationAdaptedRealtimeTwist(RealtimeTwist other, RealtimeRotation orientationChange) {
		super(orientationChange, other);
		this.other = other;
		this.orientationChange = orientationChange;
	}

	public RealtimeRotation getOrientationChange() {
		return orientationChange;
	}

	public RealtimeTwist getOther() {
		return other;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((OrientationAdaptedRealtimeTwist) obj).other)
				&& orientationChange.equals(((OrientationAdaptedRealtimeTwist) obj).orientationChange);
	}

	@Override
	protected Twist calculateCheapValue() {
		Twist twist = other.getCheapValue();
		Rotation ori = orientationChange.getCheapValue();

		// TODO: check if this is correct, and change the alias factory
		// accordingly
		return twist == null || ori == null ? null
				: new Twist(ori.apply(twist.getTransVel()), ori.apply(twist.getRotVel()));
	}

	@Override
	public int hashCode() {
		return classHash(other, orientationChange);
	}

	@Override
	public RealtimeTwist changeOrientation(RealtimeRotation orientationChange) {
		if (CONSTANT_FOLDING) {
			return new OrientationAdaptedRealtimeTwist(other, orientationChange.multiply(getOrientationChange()));
		}
		return super.changeOrientation(orientationChange);
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	public String toString() {
		return "adaptOrientation(" + other + ", " + orientationChange + ")";
	}
}
