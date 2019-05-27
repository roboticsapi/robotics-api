/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.dioprotocol;

import java.util.HashMap;
import java.util.Map;

public class DIOParameterMap extends DIOParameter {
	private final Map<String, DIOParameter> map;

	public DIOParameterMap() {
		map = new HashMap<String, DIOParameter>();
	}

	public void addParameter(String key, DIOParameter value) {
		map.put(key, value);
	}

	public Map<String, DIOParameter> getMap() {
		return map;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");

		boolean first = true;
		for (String key : map.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}

			if (key.matches("[a-zA-Z_][a-zA-Z_0-9]*")) {
				sb.append(key);
			} else {
				sb.append(new DIOString(key).toString());
			}
			sb.append(":");

			sb.append(map.get(key).toString());
		}

		sb.append("}");

		return sb.toString();
	}

	public <T extends DIOParameter> T getParameter(String key, Class<T> type) {
		if (!map.containsKey(key)) {
			throw new IllegalArgumentException("Key " + key + " not found.");
		}

		if (type.isAssignableFrom(map.get(key).getClass())) {
			return type.cast(map.get(key));
		}

		throw new IllegalArgumentException(
				"wrong type: expected " + type.getName() + ", got: " + map.get(key).getClass().getName());
	}
}
