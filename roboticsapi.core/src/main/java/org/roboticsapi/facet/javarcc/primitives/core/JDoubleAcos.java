/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JDoubleAcos extends JPrimitive {
	JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propValue = add("Value", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		RPIdouble value = inValue.get(propValue);
		if (value == null)
			outValue.set(null);
		else
			outValue.set(new RPIdouble(Math.acos(value.get())));
	}
}
