/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

/**
 * This class implements a {@link ComparatorBooleanSensor} that checks if the
 * value of its left sensor equals the value of its right sensor.
 */
public final class DoubleEqualsSensor extends ComparatorBooleanSensor<Double> {

	/**
	 * The comparison epsilon.
	 */
	private final double epsilon;

	/**
	 * Constructor.
	 * 
	 * @param left    sensor for the left part of the comparison.
	 * @param right   sensor for the right part of the comparison.
	 * @param epsilon the epsilon.
	 */
	public DoubleEqualsSensor(Sensor<Double> left, Sensor<Double> right, double epsilon) {
		super(left, right);

		this.epsilon = epsilon;
	}

	/**
	 * Constructor.
	 * 
	 * @param left    sensor for the left part of the comparison.
	 * @param right   constant value for the right part of the comparison.
	 * @param epsilon the epsilon.
	 */
	public DoubleEqualsSensor(Sensor<Double> left, double right, double epsilon) {
		this(left, new ConstantDoubleSensor(right), epsilon);
	}

	/**
	 * Constructor.
	 * 
	 * @param left    constant value for the left part of the comparison.
	 * @param right   sensor for the right part of the comparison.
	 * @param epsilon the epsilon.
	 */
	public DoubleEqualsSensor(double left, Sensor<Double> right, double epsilon) {
		this(new ConstantDoubleSensor(left), right, epsilon);
	}

	/**
	 * Constructor.
	 * 
	 * @param left    constant value for the left part of the comparison.
	 * @param right   constant value for the right part of the comparison.
	 * @param epsilon the epsilon.
	 */
	public DoubleEqualsSensor(double left, double right, double epsilon) {
		this(left, new ConstantDoubleSensor(right), epsilon);
	}

	/**
	 * Return the epsilon.
	 * 
	 * @return the epsilon.
	 */
	public double getEpsilon() {
		return epsilon;
	}

	@Override
	public String toString() {
		return "(" + getLeft() + " == " + getRight() + ")";
	}

	@Override
	protected Boolean computeCheapValue(Double leftValue, Double rightValue) {
		if (leftValue.equals(rightValue)) {
			return true;
		}
		return Math.abs(leftValue - rightValue) <= epsilon;
	}

	@Override
	protected boolean equals2(Object obj) {
		return epsilon == ((DoubleEqualsSensor) obj).epsilon;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { epsilon };
	}
}
