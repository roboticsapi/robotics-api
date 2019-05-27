/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc;

public class JParameter<T> {
	T value;
	JPrimitive primitive;
	private String name;

	public JParameter(T defaultValue) {
		value = defaultValue;
	}

	public JParameter() {
	}

	@Override
	public String toString() {
		return primitive + "." + name + "[" + value + "]";
	}

	public void set(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	protected void setPrimitive(String name, JPrimitive primitive) {
		this.name = name;
		this.primitive = primitive;
	}

	public JPrimitive getPrimitive() {
		return primitive;
	}
}
