/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public final class RealtimeBooleanAtTime extends RealtimeBoolean {

	private final RealtimeBoolean value;
	private final RealtimeDouble age;
	private final double maxAge;

	RealtimeBooleanAtTime(RealtimeBoolean value, RealtimeDouble age, double maxAge) {
		super(value, age);
		this.value = value;
		this.age = age;
		this.maxAge = maxAge;
	}

	public RealtimeBoolean getOtherValue() {
		return value;
	}

	public RealtimeDouble getAge() {
		return age;
	}

	public double getMaxAge() {
		return maxAge;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value.equals(((RealtimeBooleanAtTime) obj).value)
				&& age.equals(((RealtimeBooleanAtTime) obj).age) && maxAge == ((RealtimeBooleanAtTime) obj).maxAge;
	}

	@Override
	public int hashCode() {
		return classHash(value, age, maxAge);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(value, age);
	}

	@Override
	public String toString() {
		return "atTime(" + value + ", " + age + ")";
	}

}
