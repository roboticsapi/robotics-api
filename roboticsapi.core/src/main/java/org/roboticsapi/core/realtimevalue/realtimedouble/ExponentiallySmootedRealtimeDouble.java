/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

public final class ExponentiallySmootedRealtimeDouble extends RealtimeDouble {

	private final double halfLife;
	private final RealtimeDouble other;

	ExponentiallySmootedRealtimeDouble(RealtimeDouble other, double halfLife) {
		super(other);
		this.other = other;
		this.halfLife = halfLife;
	}

	public double getHalfLife() {
		return halfLife;
	}

	public RealtimeDouble getOther() {
		return other;
	}

	@Override
	protected Double calculateCheapValue() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && halfLife == ((ExponentiallySmootedRealtimeDouble) obj).halfLife
				&& other.equals(((ExponentiallySmootedRealtimeDouble) obj).other);
	}

	@Override
	public int hashCode() {
		return classHash(other, halfLife);
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	public String toString() {
		return "exponentialSmoothing(" + other + ", " + halfLife + ")";
	}
}
