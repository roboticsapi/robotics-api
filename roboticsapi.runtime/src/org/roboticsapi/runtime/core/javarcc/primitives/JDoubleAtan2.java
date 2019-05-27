/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JDoubleAtan2 extends JPrimitive {
	JInPort<RPIdouble> inY = add("inY", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inX = add("inX", new JInPort<RPIdouble>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propY = add("Y", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propX = add("X", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		RPIdouble y = inY.get(propY);
		RPIdouble x = inX.get(propX);
		if (anyNull(x, y))
			return;
		outValue.set(new RPIdouble(Math.atan2(y.get(), x.get())));
	}
}
