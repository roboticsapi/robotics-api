/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JClock extends JPrimitive {
	JInPort<RPIbool> inActive = add("inActive", new JInPort<RPIbool>());
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
		if (inActive.get() == null || inActive.get().get()) {
			if (inReset.get() != null && inReset.get().get() == true)
				current = 0;
			RPIdouble increment = inIncrement.get(propIncrement);
			if (increment == null)
				outValue.set(null);
			else
				outValue.set(new RPIdouble(current = current + increment.get() * getNet().getCycleTime()));
		}
	}
}
