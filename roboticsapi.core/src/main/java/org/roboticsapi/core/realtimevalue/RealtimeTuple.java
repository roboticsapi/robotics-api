/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public final class RealtimeTuple extends RealtimeValue<Object[]> {

	private final RealtimeValue<?>[] values;

	public static RealtimeTuple createFromComponents(RealtimeValue<?>... values) {
		return new RealtimeTuple(values);
	}

	RealtimeTuple(RealtimeValue<?>... values) {
		super(values);
		this.values = values;
	}

	public RealtimeValue<?>[] getValues() {
		return values;
	}

	@Override
	protected Object[] calculateCheapValue() {
		Object[] ret = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = values[i].getCheapValue();
			if (ret[i] == null) {
				return null;
			}
		}
		return ret;
	}

	@Override
	public RealtimeTuple substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeTuple) substitutionMap.get(this);
		}
		RealtimeValue<?>[] subst = new RealtimeValue<?>[values.length];
		for (int i = 0; i < values.length; i++) {
			subst[i] = values[i].substitute(substitutionMap);
		}
		return new RealtimeTuple(subst);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(values);
	}

	@Override
	public boolean isConstant() {
		for (RealtimeValue<?> v : values) {
			if (!v.isConstant()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public RealtimeValue<Object[]> fromHistory(RealtimeDouble age, double maxAge) {
		RealtimeValue<?>[] hist = new RealtimeValue<?>[values.length];
		for (int i = 0; i < values.length; i++) {
			hist[i] = values[i].fromHistory(age, maxAge);
		}
		return new RealtimeTuple(hist);
	}

	@Override
	public RealtimeBoolean isNull() {
		RealtimeBoolean ret = RealtimeBoolean.TRUE;
		for (RealtimeValue<?> v : values) {
			ret = ret.and(v.isNull());
		}
		return ret;
	}

}
