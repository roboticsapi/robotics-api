/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import java.util.Map;
import java.util.function.Function;

import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class RealtimeDerivedValue<T, U> extends RealtimeValue<T> {

	private final RealtimeValue<U> innerValue;
	private final Function<U, T> converter;

	public RealtimeDerivedValue(RealtimeValue<U> innerValue, Function<U, T> converter) {
		super(innerValue);
		this.innerValue = innerValue;
		this.converter = converter;
	}

	public final RealtimeValue<U> getRealtimeInteger() {
		return innerValue;
	}

	@Override
	public final Observer<?> createObserver(RealtimeValueListener<T> listener, RealtimeBoolean condition,
			boolean async) {
		return innerValue.createObserver(new RealtimeValueListener<U>() {
			@Override
			public void onValueChanged(U newValue) {
				if (newValue == null) {
					listener.onValueChanged(null);
				}
				listener.onValueChanged(converter.apply(newValue));

			}
		}, condition, async);
	}

	@Override
	protected final T calculateCheapValue() {
		U cheapValue = innerValue.getCheapValue();
		if (cheapValue == null) {
			return null;
		}
		return converter.apply(cheapValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final RealtimeDerivedValue<T, U> substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeDerivedValue<T, U>) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeDerivedValue<T, U> performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new RealtimeDerivedValue<>(innerValue.substitute(substitutionMap), converter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && innerValue.equals(((RealtimeDerivedValue<T, U>) obj).innerValue);
	}

	@Override
	public final int hashCode() {
		return classHash(innerValue);
	}

	@Override
	public final boolean isAvailable() {
		return innerValue.isAvailable();
	}

	@Override
	public final RealtimeBoolean isNull() {
		return innerValue.isNull();
	}

	@Override
	public final RealtimeValue<T> fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeDerivedValue<T, U>(innerValue.fromHistory(age, maxAge), converter);
	}

}
