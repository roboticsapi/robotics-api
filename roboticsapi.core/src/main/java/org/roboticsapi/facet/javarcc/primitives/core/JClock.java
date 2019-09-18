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
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JClock extends JPrimitive {
	JInPort<RPIdouble> inIncrement = add("inIncrement", new JInPort<RPIdouble>());
	JInPort<RPIbool> inReset = add("inReset", new JInPort<RPIbool>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propIncrement = add("Increment", new JParameter<RPIdouble>());
	double current = 0;

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		if (inReset.get() != null && inReset.get().get() == true)
			current = 0;
		RPIdouble increment = inIncrement.get(propIncrement);
		if (increment == null)
			outValue.set(null);
		else
			outValue.set(new RPIdouble(current = current + increment.get() * getNet().getCycleTime()));
	}
}
