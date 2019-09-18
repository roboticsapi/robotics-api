/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;

public class RealtimePair<T, U> extends RealtimeStruct<Pair<T, U>> {

	private final RealtimeValue<T> first;
	private final RealtimeValue<U> second;

	public static <T, U> RealtimePair<T, U> createFromComponents(RealtimeValue<T> first, RealtimeValue<U> second) {
		return new RealtimePair<>(first, second);
	}

	public RealtimePair(RealtimeValue<T> first, RealtimeValue<U> second) {
		super(first, second);
		this.first = first;
		this.second = second;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RealtimeValue<Pair<T, U>> createRealtimeStruct(RealtimeValue<?>... values) {
		return new RealtimePair<T, U>((RealtimeValue<T>) values[0], (RealtimeValue<U>) values[1]);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Pair<T, U> createStruct(Map<RealtimeValue<?>, Object> values) {
		return new Pair<T, U>((T) values.get(first), (U) values.get(second));
	}

	public RealtimeValue<T> getFirst() {
		return first;
	}

	public RealtimeValue<U> getSecond() {
		return second;
	}

}
