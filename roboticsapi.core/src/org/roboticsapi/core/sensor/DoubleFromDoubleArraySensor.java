/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class DoubleFromDoubleArraySensor extends UnaryFunctionDoubleSensor<Double[]> {

	private final int index;

	public DoubleFromDoubleArraySensor(DoubleArraySensor doubleArraySensor, int index) {
		super(doubleArraySensor);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public DoubleArraySensor getDoubleArray() {
		return (DoubleArraySensor) getInnerSensor();
	}

	@Override
	protected boolean equals2(UnaryFunctionDoubleSensor<?> obj) {
		return index == ((DoubleFromDoubleArraySensor) obj).index;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { index };
	}

	@Override
	public String toString() {
		return getDoubleArray() + "[" + index + "]";
	}

	@Override
	protected Double computeCheapValue(Double[] value) {
		return value[index];
	}
}
