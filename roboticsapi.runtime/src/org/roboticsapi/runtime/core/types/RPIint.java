/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.types;

import java.util.regex.Pattern;

import org.roboticsapi.runtime.rpi.types.PrimitiveType;

public class RPIint extends PrimitiveType {
	static Pattern doublePattern = Pattern.compile("\\s*([-0-9]+)\\s*(.*)");

	private int value;

	public RPIint(int value) {
		super(doublePattern);
		this.value = value;
	}

	public RPIint() {
		this(0);
	}

	public RPIint(String value) {
		this();
		consumeString(value);
	}

	public int get() {
		return value;
	}

	public void set(int value) {
		this.value = value;
	}

	@Override
	public void appendString(StringBuilder ret) {
		ret.append(value);
	}

	@Override
	protected void consumeValue(String string) {
		this.value = java.lang.Integer.valueOf(string);
	}

}
