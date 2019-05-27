/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

public class UnknownPrimitive extends Primitive {
	public UnknownPrimitive(String type) {
		super(type);
	}

	@Override
	public void add(InPort... ins) {
		super.add(ins);
	}

	@Override
	public void add(OutPort... outs) {
		super.add(outs);
	}

	@Override
	public void add(Parameter<?>... params) {
		super.add(params);
	}
}
