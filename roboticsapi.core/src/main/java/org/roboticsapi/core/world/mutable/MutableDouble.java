/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.mutable;

public class MutableDouble {
	double value;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public static boolean near(double a, double b) {
		return Math.abs(a - b) < 0.00001;
	}

	public boolean isNear(double other) {
		return near(value, other);
	}

	public boolean isNear(MutableDouble other) {
		return near(value, other.getValue());
	}

}
