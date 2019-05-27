/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

public class FragmentInPort extends InPort {

	private final OutPort internalOutPort;

	public FragmentInPort(String name) {
		super(name);
		internalOutPort = new OutPort(name);
	}

	public void setName(String name) {
		this.name = name;
		internalOutPort.name = name;
	}

	public OutPort getInternalOutPort() {
		return internalOutPort;
	}

	@Override
	public void addToPrimitive(Primitive primitive) {
		super.addToPrimitive(primitive);
		internalOutPort.addToPrimitive(primitive);
	}
}
