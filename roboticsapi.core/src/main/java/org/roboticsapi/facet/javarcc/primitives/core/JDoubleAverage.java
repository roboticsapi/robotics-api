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

public class JDoubleAverage extends JPrimitive {
	private JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());
	private JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	private JParameter<RPIdouble> propDuration = add("Duration", new JParameter<RPIdouble>());
	private Double[] history = null;
	private int count, current = 0;
	private double sum = 0;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		if (history == null) {
			count = (int) Math.ceil((propDuration.get().get() / getNet().getCycleTime()));
			history = new Double[count];
		}

		if (current >= count && history[current % count] != null)
			sum -= history[current % count];
		history[current % count] = inValue.get().get();
		if (history[current % count] != null)
			sum += history[current % count];
		current++;
		outValue.set(new RPIdouble(sum / (current >= count ? count : current)));
	}
}
