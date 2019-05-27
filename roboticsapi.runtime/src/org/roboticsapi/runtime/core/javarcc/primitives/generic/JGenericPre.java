/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JGenericPre<T> extends JPrimitive {
	T lastValue;
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		lastValue = inValue.get();
	}

	@Override
	public void writeActuator() {
		outValue.setForNextCycle(lastValue);
	}

}
