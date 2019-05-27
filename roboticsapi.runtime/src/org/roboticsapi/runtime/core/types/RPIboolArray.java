/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.types;

import org.roboticsapi.runtime.rpi.types.ArrayType;

/**
 * Array of RPICore::bool a.k.a Core::bool[]
 */
public class RPIboolArray extends ArrayType<org.roboticsapi.runtime.core.types.RPIbool> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIboolArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIboolArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIboolArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.runtime.core.types.RPIbool getInitialValue() {
		return new org.roboticsapi.runtime.core.types.RPIbool();
	}
}
