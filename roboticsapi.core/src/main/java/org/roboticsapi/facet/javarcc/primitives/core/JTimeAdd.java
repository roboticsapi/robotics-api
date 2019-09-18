/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JTimeAdd extends JPrimitive {
	JInPort<Long> inFirst = add("inTime", new JInPort<Long>());
	JInPort<RPIdouble> inSecond = add("inDelta", new JInPort<RPIdouble>());
	JOutPort<Long> outValue = add("outValue", new JOutPort<Long>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFirst, inSecond);
	}

	@Override
	public void updateData() {
		if (anyNull(inFirst, inSecond))
			return;
		outValue.set((long) (inFirst.get() + inSecond.get().get() * 1000));
	}
}
