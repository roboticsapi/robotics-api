/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc;

public class JOutPort<T> {
	private T value;
	private JPrimitive primitive;
	private int cycle;
	private String name;

	@Override
	public String toString() {
		return primitive.toString() + "." + name + " [" + get() + "]";
	}

	public void set(T value) {
		this.value = value;
		cycle = primitive.getNet().getCycle();
	}

	public void setForNextCycle(T value) {
		this.value = value;
		cycle = primitive.getNet().getCycle() + 1;
	}

	protected T get() {
		return value;
	}

	public JPrimitive getPrimitive() {
		return primitive;
	}

	protected void setPrimitive(String name, JPrimitive primitive) {
		this.name = name;
		this.primitive = primitive;
	}

	protected int getWriteCycle() {
		return cycle;
	}
}
