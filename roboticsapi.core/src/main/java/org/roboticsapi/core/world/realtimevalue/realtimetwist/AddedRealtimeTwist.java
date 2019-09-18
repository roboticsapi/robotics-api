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

public final class AddedRealtimeTwist extends RealtimeTwist {

	@Override
	protected RealtimeTwist performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new AddedRealtimeTwist(left.substitute(substitutionMap), right.substitute(substitutionMap));
	}

	private final RealtimeTwist left;
	private final RealtimeTwist right;

	AddedRealtimeTwist(RealtimeTwist left, RealtimeTwist right) {
		super(left, right);
		this.left = left;
		this.right = right;
	}

	public RealtimeTwist getLeft() {
		return left;
	}

	public RealtimeTwist getRight() {
		return right;
	}

	@Override
	protected Twist calculateCheapValue() {
		Twist s1val = getLeft().getCheapValue();
		Twist s2val = getRight().getCheapValue();
		return (s1val == null || s2val == null) ? null
				: new Twist(s1val.getTransVel().add(s2val.getTransVel()), s1val.getRotVel().add(s2val.getRotVel()));
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && left.equals(((AddedRealtimeTwist) obj).left)
				&& right.equals(((AddedRealtimeTwist) obj).right);
	}

	@Override
	public int hashCode() {
		return classHash(left, right);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(left, right);
	}

	@Override
	public String toString() {
		return "(" + left + " + " + right + ")";
	}
}
