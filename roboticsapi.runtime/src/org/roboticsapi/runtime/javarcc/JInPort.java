/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc;

public class JInPort<T> {
	private JOutPort<T> source;
	private JPrimitive primitive;
	protected String name;

	@Override
	public String toString() {
		return primitive + "." + name + " (connected to " + source + ")";
	}

	public T get() {
		if (getSource() == null)
			return null;
		if (getCurrentCycle() == getSource().getWriteCycle())
			return getSource().get();
		else
			return null;
	}

	public T get(JParameter<T> defaultValue) {
		if (getSource() == null || get() == null)
			return defaultValue.get();
		return get();
	}

	public T get(T defaultValue) {
		if (getSource() == null || get() == null)
			return defaultValue;
		return get();
	}

	public void connectTo(JOutPort<T> source) {
		this.source = source;
	}

	public JPrimitive getPrimitive() {
		return primitive;
	}

	protected void setPrimitive(String name, JPrimitive primitive) {
		this.name = name;
		this.primitive = primitive;
	}

	protected int getCurrentCycle() {
		return primitive.getNet().getCycle();
	}

	protected JOutPort<T> getSource() {
		return source;
	}

	public boolean isConnected() {
		return source != null;
	}
}
