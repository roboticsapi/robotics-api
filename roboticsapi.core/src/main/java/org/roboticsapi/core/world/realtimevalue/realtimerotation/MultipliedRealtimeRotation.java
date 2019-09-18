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

public final class MultipliedRealtimeRotation extends RealtimeRotation {

	@Override
	protected RealtimeRotation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new MultipliedRealtimeRotation(left.invert(), right.invert());
	}

	private final RealtimeRotation left;
	private final RealtimeRotation right;

	MultipliedRealtimeRotation(RealtimeRotation left, RealtimeRotation right) {
		super(left, right);
		this.left = left;
		this.right = right;
	}

	@Override
	protected Rotation calculateCheapValue() {
		Rotation first = left.getCheapValue();
		Rotation second = right.getCheapValue();

		return (first != null && second != null) ? first.multiply(second) : null;
	}

	public RealtimeRotation getLeft() {
		return left;
	}

	public RealtimeRotation getRight() {
		return right;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && right.equals(((MultipliedRealtimeRotation) obj).right)
				&& left.equals(((MultipliedRealtimeRotation) obj).left);
	}

	@Override
	public int hashCode() {
		return classHash(right, left);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(left, right);
	}

	@Override
	public String toString() {
		return "(" + right + " * " + left + ")";
	}
}
