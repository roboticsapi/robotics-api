/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public abstract class RealtimeStruct<T> extends RealtimeValue<T> {

	private final RealtimeTuple tuple;
	private final List<RealtimeValue<?>> values = new ArrayList<>();

	public RealtimeStruct(RealtimeValue<?>... values) {
		super(values);
		for (RealtimeValue<?> v : values) {
			this.values.add(v);
		}
		tuple = new RealtimeTuple(values);
		addInnerValues(tuple);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RealtimeValue<T> substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeValue<T>) substitutionMap.get(this);
		}
		RealtimeTuple newTuple = tuple.substitute(substitutionMap);
		return createRealtimeStruct(newTuple.getValues());
	}

	protected abstract RealtimeValue<T> createRealtimeStruct(RealtimeValue<?>... values);

	protected abstract T createStruct(Map<RealtimeValue<?>, Object> values);

	@Override
	public Observer<?> createObserver(RealtimeValueListener<T> listener, RealtimeBoolean condition, boolean async) {
		return tuple.createObserver(new RealtimeValueListener<Object[]>() {
			@Override
			public void onValueChanged(Object[] newValue) {
				listener.onValueChanged(wrap(newValue));
			}

		}, condition, async);
	}

	private T wrap(Object[] newValue) {
		if (newValue == null) {
			return null;
		} else {
			Map<RealtimeValue<?>, Object> valueMap = new HashMap<>();
			for (int i = 0; i < values.size(); i++) {
				valueMap.put(values.get(i), newValue[i]);
			}
			return createStruct(valueMap);
		}
	}

	@Override
	protected T calculateCheapValue() {
		return wrap(tuple.getCheapValue());
	}

	@Override
	public boolean isAvailable() {
		return tuple.isAvailable();
	}

	@Override
	public RealtimeValue<T> fromHistory(RealtimeDouble age, double maxAge) {
		RealtimeValue<?>[] historyValues = new RealtimeValue<?>[values.size()];
		for (int i = 0; i < values.size(); i++) {
			historyValues[i] = values.get(i).fromHistory(age, maxAge);
		}
		return createRealtimeStruct(historyValues);
	}

	@Override
	public RealtimeBoolean isNull() {
		return tuple.isNull();
	}

}
