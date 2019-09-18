/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.RealtimeValue;

public class ConsistentTimeRealtimeDouble extends RealtimeDouble {

	@Override
	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		List<RealtimeValue<?>> subst = new ArrayList<RealtimeValue<?>>();
		for (RealtimeValue<?> rv : values) {
			subst.add(rv.substitute(substitutionMap));
		}
		return new ConsistentTimeRealtimeDouble(maxAge, epsilon, subst);
	}

	private final List<RealtimeValue<?>> values = new ArrayList<RealtimeValue<?>>();
	private final double maxAge;
	private final double epsilon;

	public ConsistentTimeRealtimeDouble(double maxAge, double epsilon, Collection<RealtimeValue<?>> values) {
		super(values.toArray(new RealtimeValue<?>[values.size()]));
		this.maxAge = maxAge;
		this.epsilon = epsilon;
		for (RealtimeValue<?> value : values) {
			if (!value.isConstant()) {
				this.values.add(value);
			}
		}
	}

	public ConsistentTimeRealtimeDouble(double maxAge, double epsilon, RealtimeValue<?>[] values) {
		this(maxAge, epsilon, Arrays.asList(values));
	}

	public double getMaxAge() {
		return maxAge;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public List<RealtimeValue<?>> getValues() {
		return values;
	}

	@Override
	public boolean isAvailable() {
		for (RealtimeValue<?> value : values) {
			if (!value.isAvailable()) {
				return false;
			}
		}
		return true;
	}

}
