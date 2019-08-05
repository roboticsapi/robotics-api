/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.ComparatorBooleanSensor;

public class MockComparatorBooleanSensor<T> extends ComparatorBooleanSensor<T> {
	public MockComparatorBooleanSensor(Sensor<T> left, Sensor<T> right) {
		super(left, right);
	}

	@Override
	protected Boolean computeCheapValue(T leftValue, T rightValue) {
		if ((leftValue == null) || (rightValue == null)) {
			throw new IllegalArgumentException("None of the values may be null.");
		}

		return leftValue.equals(rightValue);
	}
}
