/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;

public final class DataAgeRealtimeDouble extends RealtimeDouble {

	@Override
	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new DataAgeRealtimeDouble(sensor.substitute(substitutionMap));
	}

	private final RealtimeValue<?> sensor;

	public DataAgeRealtimeDouble(RealtimeValue<?> sensor) {
		super(sensor);
		this.sensor = sensor;
	}

	public RealtimeValue<?> getSensor() {
		return sensor;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensor.equals(((DataAgeRealtimeDouble) obj).sensor);
	}

	@Override
	public int hashCode() {
		return classHash(sensor);
	}

	@Override
	public boolean isAvailable() {
		return sensor.isAvailable();
	}

	@Override
	public String toString() {
		return "age(" + sensor + ")";
	}
}
