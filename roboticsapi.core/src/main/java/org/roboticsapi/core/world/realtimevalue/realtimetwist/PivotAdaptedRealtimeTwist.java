/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class PivotAdaptedRealtimeTwist extends RealtimeTwist {

	@Override
	protected RealtimeTwist performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new PivotAdaptedRealtimeTwist(other.substitute(substitutionMap),
				pivotChange.substitute(substitutionMap));
	}

	private final RealtimeTwist other;
	private final RealtimeVector pivotChange;

	PivotAdaptedRealtimeTwist(RealtimeTwist other, RealtimeVector pivotChange) {
		super(other, pivotChange);
		this.other = other;
		this.pivotChange = pivotChange;
	}

	public RealtimeTwist getOther() {
		return other;
	}

	public RealtimeVector getPivotChange() {
		return pivotChange;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((PivotAdaptedRealtimeTwist) obj).other)
				&& getPivotChange().equals(((PivotAdaptedRealtimeTwist) obj).getPivotChange());
	}

	@Override
	public int hashCode() {
		return classHash(other, pivotChange);
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	protected Twist calculateCheapValue() {
		Twist twist = other.getCheapValue();
		Vector pivot = pivotChange.getCheapValue();

		// TODO: check if this is correct, and adapt alias factory accordingly
		return twist == null || pivot == null ? null
				: new Twist(twist.getTransVel().add(twist.getRotVel().cross(pivot)), twist.getRotVel());
	}

	@Override
	public String toString() {
		return "adaptPivot(" + other + ", " + pivotChange + ")";
	}
}
