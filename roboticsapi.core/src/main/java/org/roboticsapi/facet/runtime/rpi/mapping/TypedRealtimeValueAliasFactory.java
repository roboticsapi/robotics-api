/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.RealtimeValue;

public abstract class TypedRealtimeValueAliasFactory<U, V extends RealtimeValue<U>>
		implements RealtimeValueAliasFactory {
	private final Class<V> type;

	public TypedRealtimeValueAliasFactory(Class<V> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> RealtimeValue<T> createRealtimeValueAlias(RealtimeValue<T> value) throws MappingException {
		if (type.isAssignableFrom(value.getClass()))
			return (RealtimeValue<T>) createAlias(type.cast(value));
		return null;
	}

	protected abstract RealtimeValue<U> createAlias(V value);

}
