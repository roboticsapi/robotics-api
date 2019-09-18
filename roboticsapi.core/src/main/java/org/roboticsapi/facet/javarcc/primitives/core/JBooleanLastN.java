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
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

public class JBooleanLastN extends JPrimitive {
	private JInPort<RPIbool> inValue = add("inValue", new JInPort<RPIbool>());
	private JOutPort<RPIbool> outNone = add("outNone", new JOutPort<RPIbool>());
	private JOutPort<RPIbool> outAll = add("outAll", new JOutPort<RPIbool>());
	private JOutPort<RPIint> outCount = add("outCount", new JOutPort<RPIint>());
	private JParameter<RPIint> propCount = add("Count", new JParameter<RPIint>());
	private ArrayList<RPIbool> history = null;
	private int count = 0, numTrue = 0, current = 0;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			if (history == null) {
				count = propCount.get().get();
				history = new ArrayList<RPIbool>(count);
			}

		if (current >= count && history.get(current % count) != null && history.get(current % count).get() == true)
			numTrue--;
		history.set(current % count, inValue.get());
		if (history.get(current % count) != null && history.get(current % count).get() == true)
			numTrue++;

		current++;
		outCount.set(new RPIint(numTrue));
		outAll.set(new RPIbool(numTrue == (current >= count ? count : current)));
		outNone.set(new RPIbool(numTrue == 0));
	}
}
