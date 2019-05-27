/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.dioprotocol;

import java.util.ArrayList;
import java.util.List;

public class DIOCommand {
	private final String command;
	private final List<DIOParameter> parameters;

	public DIOCommand(String command, DIOParameter... dioParameters) {
		this.command = command;

		parameters = new ArrayList<DIOParameter>();

		for (DIOParameter param : dioParameters) {
			parameters.add(param);
		}
	}

	public void addParameter(DIOParameter parameter) {
		parameters.add(parameter);
	}

	public String getCommand() {
		return command;
	}

	public int getParameterSize() {
		return parameters.size();
	}

	public DIOParameter getParameter(int index) {
		return parameters.get(index);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(command);
		sb.append("(");

		boolean first = true;
		for (DIOParameter p : parameters) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(p.toString());
		}

		sb.append(")");

		return sb.toString();
	}
}
