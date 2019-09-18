/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public final class RealtimeVectorAtTime extends RealtimeVector {

	private final RealtimeVector vector;
	private final RealtimeDouble age;
	private final double maxAge;

	RealtimeVectorAtTime(RealtimeVector vector, RealtimeDouble age, double maxAge) {
		super(vector, age);
		this.vector = vector;
		this.age = age;
		this.maxAge = maxAge;
	}

	public RealtimeVector getVector() {
		return vector;
	}

	public RealtimeDouble getAge() {
		return age;
	}

	public double getMaxAge() {
		return maxAge;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && vector.equals(((RealtimeVectorAtTime) obj).vector)
				&& age.equals(((RealtimeVectorAtTime) obj).age) && maxAge == ((RealtimeVectorAtTime) obj).maxAge;
	}

	@Override
	public int hashCode() {
		return classHash(vector, age, maxAge);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(age, vector);
	}

	@Override
	public String toString() {
		return "atTime(" + vector + ", " + age + ")";
	}
}
