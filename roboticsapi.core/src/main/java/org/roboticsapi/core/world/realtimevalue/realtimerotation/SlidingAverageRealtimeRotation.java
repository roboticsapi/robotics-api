/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Rotation;

public class SlidingAverageRealtimeRotation extends RealtimeRotation {

	@Override
	protected RealtimeRotation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new SlidingAverageRealtimeRotation(other.substitute(substitutionMap), duration);
	}

	private final RealtimeRotation other;
	private final double duration;

	SlidingAverageRealtimeRotation(RealtimeRotation other, double duration) {
		super(other);
		this.other = other;
		this.duration = duration;
	}

	public RealtimeRotation getOther() {
		return other;
	}

	public double getDuration() {
		return duration;
	}

	@Override
	protected Rotation calculateCheapValue() {
		if (other.getRuntime() == null) {
			throw new IllegalArgumentException("Cannot calculate sliding average for RealtimeValue without runtime");
		} else {
			return super.calculateCheapValue();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && duration == ((SlidingAverageRealtimeRotation) obj).duration
				&& other.equals(((SlidingAverageRealtimeRotation) obj).other);
	}

	@Override
	public int hashCode() {
		return classHash(other, duration);
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	public String toString() {
		return "slidingAverage(" + other + ", " + duration + ")";
	}

}
