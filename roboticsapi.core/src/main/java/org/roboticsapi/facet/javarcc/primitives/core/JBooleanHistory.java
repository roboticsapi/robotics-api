/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import java.util.ArrayList;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JBooleanHistory extends JPrimitive {
	JInPort<RPIbool> inValue = add("inValue", new JInPort<RPIbool>());
	JOutPort<RPIbool> outNone = add("outNone", new JOutPort<RPIbool>());
	JOutPort<RPIbool> outAll = add("outAll", new JOutPort<RPIbool>());
	JOutPort<RPIdouble> outAmount = add("outAmount", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propDuration = add("Duration", new JParameter<RPIdouble>());
	ArrayList<RPIbool> history = null;
	int count, current = 0, numTrue = 0;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		if (history == null) {
			count = (int) (propDuration.get().get() / getNet().getCycleTime());
			current = 0;
			numTrue = 0;
			history = new ArrayList<RPIbool>(count);
		}

		if (current >= count && history.get(current % count) != null && history.get(current % count).get() == true)
			numTrue--;
		history.set(current % count, inValue.get());
		if (history.get(current % count) != null && history.get(current % count).get() == true)
			numTrue++;
		current++;
		outAmount.set(new RPIdouble(((double) numTrue) / (current >= count ? count : current)));
		outAll.set(new RPIbool(numTrue == count));
		outNone.set(new RPIbool(numTrue == 0));

		super.updateData();
	}
}
