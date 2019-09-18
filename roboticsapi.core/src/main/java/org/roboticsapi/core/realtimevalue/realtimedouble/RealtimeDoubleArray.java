/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public abstract class RealtimeDoubleArray extends RealtimeValue<Double[]> {
	final int size;

	@Override
	public final RealtimeDoubleArray substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeDoubleArray) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeDoubleArray performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	/**
	 * @param runtime
	 */
	public RealtimeDoubleArray(int size, RealtimeValue<?>... values) {
		super(values);
		this.size = size;
	}

	/**
	 * @param runtime
	 */
	public RealtimeDoubleArray(final RoboticsRuntime runtime, int size) {
		super(runtime);
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public RealtimeDouble[] getDoubles() {
		RealtimeDouble[] ret = new RealtimeDouble[size];
		for (int i = 0; i < size; i++) {
			ret[i] = get(i);
		}
		return ret;
	}

	public RealtimeDouble get(int index) {
		return new GetFromArrayRealtimeDouble(this, index);
	}

	public static RealtimeDoubleArray createFromComponents(RealtimeDouble... sensors) {
		return new RealtimeDoublesToRealtimeDoubleArray(sensors);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeDoubleArrayIsNull(this);
	}

	@Override
	public RealtimeDoubleArray fromHistory(RealtimeDouble age, double maxAge) {
		// FIXME: implement
		return null;
	}

	public static RealtimeDoubleArray createFromConstants(double[] values) {
		RealtimeDouble[] sensors = new RealtimeDouble[values.length];
		for (int i = 0; i < sensors.length; i++) {
			sensors[i] = RealtimeDouble.createFromConstant(values[i]);
		}
		return createFromComponents(sensors);
	}

}
