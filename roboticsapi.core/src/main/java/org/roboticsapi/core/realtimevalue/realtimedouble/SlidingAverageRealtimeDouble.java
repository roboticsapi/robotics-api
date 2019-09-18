/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;

public final class SlidingAverageRealtimeDouble extends RealtimeDouble {

	@Override
	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new SlidingAverageRealtimeDouble(other.substitute(substitutionMap), duration);
	}

	private final RealtimeDouble other;
	private final double duration;

	SlidingAverageRealtimeDouble(RealtimeDouble other, double duration) {
		super(other);
		this.other = other;
		this.duration = duration;
	}

	public RealtimeDouble getOther() {
		return other;
	}

	public double getDuration() {
		return duration;
	}

	@Override
	public Double calculateCheapValue() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && duration == ((SlidingAverageRealtimeDouble) obj).duration
				&& other.equals(((SlidingAverageRealtimeDouble) obj).other);
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
