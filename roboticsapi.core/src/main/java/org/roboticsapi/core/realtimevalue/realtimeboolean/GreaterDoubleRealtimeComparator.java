/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * This class implements a {@link RealtimeComparator} that checks if the value
 * of its left {@link RealtimeValue} is greater than the value of its right
 * {@link RealtimeValue}.
 */
public final class GreaterDoubleRealtimeComparator extends RealtimeComparator<Double> {

	@Override
	protected RealtimeBoolean performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new GreaterDoubleRealtimeComparator((RealtimeDouble) getLeft().substitute(substitutionMap),
				(RealtimeDouble) getRight().substitute(substitutionMap));
	}

	/**
	 * Constructor.
	 *
	 * @param left  sensor for the left part of the comparison.
	 * @param right sensor for the right part of the comparison.
	 */
	GreaterDoubleRealtimeComparator(RealtimeDouble left, RealtimeDouble right) {
		super(left, right);
	}

	/**
	 * Constructor.
	 *
	 * @param left  sensor for the left part of the comparison.
	 * @param right constant value for the right part of the comparison.
	 */
	GreaterDoubleRealtimeComparator(RealtimeDouble left, double right) {
		this(left, RealtimeDouble.createFromConstant(right));
	}

	/**
	 * Constructor.
	 *
	 * @param left  constant value for the left part of the comparison.
	 * @param right sensor for the right part of the comparison.
	 */
	GreaterDoubleRealtimeComparator(double left, RealtimeDouble right) {
		this(RealtimeDouble.createFromConstant(left), right);
	}

	/**
	 * Constructor.
	 *
	 * @param left  constant value the left part of the comparison.
	 * @param right constant value the right part of the comparison.
	 */
	GreaterDoubleRealtimeComparator(double left, double right) {
		this(left, RealtimeDouble.createFromConstant(right));
	}

	@Override
	public String toString() {
		return "(" + getLeft() + " > " + getRight() + ")";
	}

	@Override
	protected Boolean computeCheapValue(Double leftValue, Double rightValue) {
		return leftValue > rightValue;
	}
}
