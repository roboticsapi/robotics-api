/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

public final class InvertedRealtimeRotation extends RealtimeRotation {

	private final RealtimeRotation other;

	InvertedRealtimeRotation(RealtimeRotation other) {
		super(other);
		this.other = other;
	}

	public RealtimeRotation getOther() {
		return other;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((InvertedRealtimeRotation) obj).other);
	}

	@Override
	public RealtimeRotation invert() {
		return other;
	}

	@Override
	public int hashCode() {
		return classHash(other);
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	public String toString() {
		return "((" + other + ") ^ -1)";
	}
}
