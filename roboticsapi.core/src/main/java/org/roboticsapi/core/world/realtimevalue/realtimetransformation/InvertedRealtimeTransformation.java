/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Transformation;

public final class InvertedRealtimeTransformation extends RealtimeTransformation {

	@Override
	protected RealtimeTransformation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new InvertedRealtimeTransformation(other.substitute(substitutionMap));
	}

	private final RealtimeTransformation other;

	InvertedRealtimeTransformation(RealtimeTransformation other) {
		super(other);
		this.other = other;
	}

	@Override
	protected Transformation calculateCheapValue() {
		Transformation cheapValue = other.getCheapValue();
		return cheapValue == null ? null : cheapValue.invert();
	}

	@Override
	public RealtimeTransformation invert() {
		return other;
	}

	public RealtimeTransformation getOther() {
		return other;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((InvertedRealtimeTransformation) obj).other);
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
		return "((" + other + ") ^ -1 )";
	}
}
