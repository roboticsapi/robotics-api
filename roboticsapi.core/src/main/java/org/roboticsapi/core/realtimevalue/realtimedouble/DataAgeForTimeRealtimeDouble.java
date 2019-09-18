/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;

public class DataAgeForTimeRealtimeDouble extends RealtimeDouble {

	@Override
	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new DataAgeForTimeRealtimeDouble(innerValue.substitute(substitutionMap),
				time.substitute(substitutionMap), maxAge);
	}

	private final RealtimeValue<?> innerValue;
	private final RealtimeDouble time;
	private final double maxAge;

	public DataAgeForTimeRealtimeDouble(RealtimeValue<?> innerValue, RealtimeDouble time, double maxAge) {
		super(innerValue, time);
		this.innerValue = innerValue;
		this.time = time;
		this.maxAge = maxAge;
	}

	public RealtimeValue<?> getInnerValue() {
		return innerValue;
	}

	public RealtimeDouble getTime() {
		return time;
	}

	public double getMaxAge() {
		return maxAge;
	}

	@Override
	public boolean isAvailable() {
		return innerValue.isAvailable();
	}

}
