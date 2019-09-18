/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RpiParameters {
	private final Map<String, String> innerMap = new HashMap<String, String>();

	public String get(String key) {
		return innerMap.get(key);
	}

	public <T extends Type> T get(Class<T> type, String key) {
		try {
			if (get(key) == null) {
				return null;
			}
			return type.getConstructor(String.class).newInstance(get(key));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public RpiParameters with(String key, String value) {
		put(key, value);
		return this;
	}

	public RpiParameters with(String key, Type value) {
		put(key, value);
		return this;
	}

	public void put(String key, String value) {
		innerMap.put(key, value);
	}

	public void put(String key, Type type) {
		innerMap.put(key, type.toString());
	}

	public Set<String> keySet() {
		return innerMap.keySet();
	}

	@Override
	public String toString() {
		return innerMap.toString();
	}

}
