/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public final class RealtimeTwistAtTime extends RealtimeTwist {

	private final RealtimeTwist other;
	private final RealtimeDouble age;
	private final double maxAge;

	RealtimeTwistAtTime(RealtimeTwist other, RealtimeDouble age, double maxAge) {
		super(other, age);
		this.other = other;
		this.age = age;
		this.maxAge = maxAge;
	}

	public RealtimeTwist getOther() {
		return other;
	}

	public RealtimeDouble getAge() {
		return age;
	}

	public double getMaxAge() {
		return maxAge;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && other.equals(((RealtimeTwistAtTime) obj).other)
				&& age.equals(((RealtimeTwistAtTime) obj).age) && maxAge == ((RealtimeTwistAtTime) obj).maxAge;
	}

	@Override
	public int hashCode() {
		return classHash(other, age, maxAge);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(age, other);
	}

	@Override
	public String toString() {
		return "atTime(" + other + ", " + age + ")";
	}
}
