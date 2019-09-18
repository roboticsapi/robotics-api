/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.RealtimeValue;

/**
 * Abstract implementation for a {@link RealtimeBoolean} that compares the
 * values of two other {@link RealtimeValues}s of type <code>T</code>.
 *
 * @param T the type of the sensors to compare
 */
public abstract class RealtimeComparator<T> extends RealtimeBoolean {

	/**
	 * The left sensor.
	 */
	private final RealtimeValue<T> left;

	/**
	 * The right sensor.
	 */
	private final RealtimeValue<T> right;

	/**
	 * Constructor.
	 *
	 * @param left  the left sensor for comparison.
	 * @param right the right sensor for comparison.
	 */
	protected RealtimeComparator(RealtimeValue<T> left, RealtimeValue<T> right) {
		super(left, right);
		this.left = left;
		this.right = right;
	}

	/**
	 * Returns the left sensor for comparison.
	 *
	 * @return the left sensor.
	 */
	public final RealtimeValue<T> getLeft() {
		return left;
	}

	/**
	 * Returns the right sensor for comparison.
	 *
	 * @return the right sensor.
	 */
	public final RealtimeValue<T> getRight() {
		return right;
	}

	@Override
	public final Boolean calculateCheapValue() {
		T leftValue = left.getCheapValue();
		if (leftValue == null) {
			return null;
		}

		T rightValue = right.getCheapValue();
		if (rightValue == null) {
			return null;
		}
		return computeCheapValue(leftValue, rightValue);
	}

	/**
	 * Computes the cheap value for the composed {@link RealtimeDouble}.
	 *
	 * @param leftValue  the left sensor's cheap value
	 * @param rightValue the second sensor's cheap value
	 * @return the computed cheap value.
	 */
	protected abstract Boolean computeCheapValue(T leftValue, T rightValue);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && left.equals(((RealtimeComparator<?>) obj).left)
				&& right.equals(((RealtimeComparator<?>) obj).right) && equals2(obj);
	}

	protected boolean equals2(Object obj) {
		return true;
	}

	@Override
	public final int hashCode() {
		int hashCode = classHash(left, right);
		return hash(hashCode, getMoreObjectsForHashCode());
	}

	/**
	 * Override to supply more objects for calculation the hash code.
	 *
	 * @return more objects for the hash code.
	 */
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[0];
	}

	@Override
	public final boolean isAvailable() {
		return areAvailable(left, right);
	}

}
