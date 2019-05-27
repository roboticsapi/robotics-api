/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.dioprotocol;

import java.util.ArrayList;
import java.util.List;

public class DIOParameterList extends DIOParameter {
	private final List<DIOParameter> parameters;

	public DIOParameterList() {
		parameters = new ArrayList<DIOParameter>();
	}

	public void addParameter(DIOParameter parameter) {
		parameters.add(parameter);
	}

	public List<DIOParameter> getList() {
		return parameters;
	}

	@Override
	public String toString() {
		boolean first = true;

		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (DIOParameter p : parameters) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(p.toString());
		}

		sb.append("]");

		return sb.toString();
	}

	public <T extends DIOParameter> T getParameter(int index, Class<T> type) {
		if (index < 0 || index >= parameters.size()) {
			throw new IllegalArgumentException("index " + index + " out of bounds");
		}

		if (type.isAssignableFrom(parameters.get(index).getClass())) {
			return type.cast(parameters.get(index));
		}

		throw new IllegalArgumentException(
				"wrong type: expected " + type.getName() + ", got: " + parameters.get(index).getClass().getName());
	}
}
