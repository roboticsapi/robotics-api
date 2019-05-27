/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.types;

import java.util.regex.Pattern;

import org.roboticsapi.runtime.rpi.types.PrimitiveType;

public class RPIbool extends PrimitiveType {
	static Pattern boolPattern = Pattern.compile("\\s*(true|false)\\s*(.*)");

	private boolean value;

	public RPIbool(boolean value) {
		super(boolPattern);
		this.value = value;
	}

	public RPIbool() {
		this(false);
	}

	public RPIbool(String value) {
		this();
		consumeString(value);
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	@Override
	public void appendString(StringBuilder ret) {
		ret.append(value ? "true" : "false");
	}

	@Override
	protected void consumeValue(String string) {
		this.value = string.equals("true");
	}

}
