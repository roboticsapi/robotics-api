/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JEdgeDetection extends JPrimitive {
	JInPort<RPIbool> inValue = add("inValue", new JInPort<RPIbool>());
	JOutPort<RPIbool> outValue = add("outValue", new JOutPort<RPIbool>());
	JParameter<RPIbool> propDirection = add("Direction", new JParameter<RPIbool>());
	RPIbool last = null;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		RPIbool direction = propDirection.get();
		RPIbool value = inValue.get();

		if (value != null && value.get() == direction.get() && (last == null || last.get() != direction.get())) {
			outValue.set(new RPIbool(true));
		} else {
			outValue.set(new RPIbool(false));
		}
		last = value;
	}
}
