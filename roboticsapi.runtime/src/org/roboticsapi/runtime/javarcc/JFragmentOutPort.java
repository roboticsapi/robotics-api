/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc;

public class JFragmentOutPort<T> extends JOutPort<T> {
	private JInPort<T> innerPort = new JInPort<T>();

	public JInPort<T> getInnerPort() {
		return innerPort;
	}

	@Override
	protected void setPrimitive(String name, JPrimitive primitive) {
		super.setPrimitive(name, primitive);
		innerPort.setPrimitive(name, primitive);
	}

	@Override
	protected T get() {
		return innerPort.get();
	}

	@Override
	protected int getWriteCycle() {
		if (innerPort.getSource() != null)
			return innerPort.getSource().getWriteCycle();
		return -1;
	}

	@Override
	public void set(T value) {
		throw new IllegalArgumentException("FragmentOutPorts cannot be written.");
	}
}
