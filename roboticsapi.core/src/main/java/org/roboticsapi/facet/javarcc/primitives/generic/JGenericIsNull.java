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

public class JGenericIsNull<T extends Type> extends JPrimitive {
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JOutPort<RPIbool> outValue = add("outValue", new JOutPort<RPIbool>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		outValue.set(new RPIbool(inValue.get() == null));
	}
}
