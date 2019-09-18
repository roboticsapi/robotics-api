/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import org.roboticsapi.core.world.Twist;

public final class InvertedRealtimeTwist extends RealtimeTwist {

	private final RealtimeTwist other;

	InvertedRealtimeTwist(RealtimeTwist other) {
		this.other = other;
	}

	public RealtimeTwist getOther() {
		return other;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((InvertedRealtimeTwist) obj).other);
	}

	@Override
	protected Twist calculateCheapValue() {
		Twist otherTwist = other.getCheapValue();
		return otherTwist == null ? null
				: new Twist(otherTwist.getTransVel().invert(), otherTwist.getRotVel().invert());
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
