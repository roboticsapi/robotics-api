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

public class JInterval extends JPrimitive {
	JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inMin = add("inMin", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inMax = add("inMax", new JInPort<RPIdouble>());
	JOutPort<RPIbool> outActive = add("outActive", new JOutPort<RPIbool>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propMin = add("Min", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propMax = add("Max", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		RPIdouble min = inMin.get(propMin), max = inMax.get(propMax);
		RPIdouble tick = inValue.get();
		if (anyNull(min, max, tick))
			return;
		if (tick.get() < min.get()) {
			outValue.set(new RPIdouble(0.0));
			outActive.set(new RPIbool(false));
		} else if (tick.get() > max.get()) {
			outValue.set(new RPIdouble(1.0));
			outActive.set(new RPIbool(false));
		} else {
			if (min.get() == max.get())
				max = new RPIdouble(min.get() + 1);
			outValue.set(new RPIdouble(1.0 * (tick.get() - min.get()) / (max.get() - min.get())));
			outActive.set(new RPIbool(true));
		}
	}
}
