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
 * value of its left sensor is greater than the value of its right sensor.
 */
public final class DoubleIsGreaterSensor extends ComparatorBooleanSensor<Double> {

	/**
	 * Constructor.
	 * 
	 * @param left  sensor for the left part of the comparison.
	 * @param right sensor for the right part of the comparison.
	 */
	public DoubleIsGreaterSensor(Sensor<Double> left, Sensor<Double> right) {
		super(left, right);
	}

	/**
	 * Constructor.
	 * 
	 * @param left  sensor for the left part of the comparison.
	 * @param right constant value for the right part of the comparison.
	 */
	public DoubleIsGreaterSensor(Sensor<Double> left, double right) {
		this(left, new ConstantDoubleSensor(right));
	}

	/**
	 * Constructor.
	 * 
	 * @param left  constant value for the left part of the comparison.
	 * @param right sensor for the right part of the comparison.
	 */
	public DoubleIsGreaterSensor(double left, Sensor<Double> right) {
		this(new ConstantDoubleSensor(left), right);
	}

	/**
	 * Constructor.
	 * 
	 * @param left  constant value the left part of the comparison.
	 * @param right constant value the right part of the comparison.
	 */
	public DoubleIsGreaterSensor(double left, double right) {
		this(left, new ConstantDoubleSensor(right));
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
