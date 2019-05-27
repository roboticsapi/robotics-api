/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

/**
 * Abstract implementation for a {@link BooleanSensor} that compares the values
 * of two other {@link Sensor}s of type <code>T</code>.
 *
 * @param T the type of the sensors to compare
 */
public abstract class ComparatorBooleanSensor<T> extends BooleanSensor {

	/**
	 * The left sensor.
	 */
	private final Sensor<T> left;

	/**
	 * The right sensor.
	 */
	private final Sensor<T> right;

	/**
	 * Constructor.
	 *
	 * @param left  the left sensor for comparison.
	 * @param right the right sensor for comparison.
	 */
	protected ComparatorBooleanSensor(Sensor<T> left, Sensor<T> right) {
		super(selectRuntime(left, right));
		addInnerSensors(left, right);
		this.left = left;
		this.right = right;
	}

	/**
	 * Returns the left sensor for comparison.
	 *
	 * @return the left sensor.
	 */
	public final Sensor<T> getLeft() {
		return left;
	}

	/**
	 * Returns the right sensor for comparison.
	 *
	 * @return the right sensor.
	 */
	public final Sensor<T> getRight() {
		return right;
	}

	@Override
	protected final Boolean calculateCheapValue() {
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
	 * Computes the cheap value for the composed {@link DoubleSensor}.
	 *
	 * @param leftValue  the left sensor's cheap value
	 * @param rightValue the second sensor's cheap value
	 * @return the computed cheap value.
	 */
	protected abstract Boolean computeCheapValue(T leftValue, T rightValue);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && left.equals(((ComparatorBooleanSensor<?>) obj).left)
				&& right.equals(((ComparatorBooleanSensor<?>) obj).right) && equals2(obj);
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
