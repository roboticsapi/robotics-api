/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.generic;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.Type;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;

public class JGenericSetNull<T extends Type> extends JPrimitive {
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JInPort<RPIbool> inNull = add("inNull", new JInPort<RPIbool>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue, inNull);
	}

	@Override
	public void updateData() {
		RPIbool beNull = inNull.get();
		if (beNull != null && beNull.get() == false)
			outValue.set(inValue.get());
		else
			outValue.set(null);
	}
}
