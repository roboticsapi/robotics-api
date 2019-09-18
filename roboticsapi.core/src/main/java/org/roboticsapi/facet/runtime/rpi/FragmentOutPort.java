/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

public class FragmentOutPort extends OutPort {
	private OutPort innerPort;

	public FragmentOutPort(String name, OutPort innerPort) {
		super(name);
		this.innerPort = innerPort;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OutPort getInnerPort() {
		return innerPort;
	}

	public void setInnerPort(OutPort innerPort) {
		this.innerPort = innerPort;
	}
}
