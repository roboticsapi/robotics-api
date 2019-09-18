/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public abstract class TypedRealtimeValueFragmentFactory<U, T extends RealtimeValue<U>>
		implements RealtimeValueFragmentFactory {
	private final Class<T> type;

	public TypedRealtimeValueFragmentFactory(Class<T> type) {
		this.type = type;
	}

	@Override
	public RealtimeValueFragment<?> createRealtimeValueFragment(RealtimeValue<?> value) throws MappingException {
		try {
			if (type.isAssignableFrom(value.getClass()))
				return createFragment(type.cast(value));
		} catch (RpiException e) {
			throw new MappingException(e);
		}
		return null;
	}

	protected abstract RealtimeValueFragment<U> createFragment(T value) throws MappingException, RpiException;

}
