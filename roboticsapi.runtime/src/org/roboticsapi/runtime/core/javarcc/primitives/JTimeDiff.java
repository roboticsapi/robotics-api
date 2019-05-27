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
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JTimeDiff extends JPrimitive {
	JInPort<Long> inFirst = add("inFirst", new JInPort<Long>());
	JInPort<Long> inSecond = add("inSecond", new JInPort<Long>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFirst, inSecond);
	}

	@Override
	public void updateData() {
		if (anyNull(inFirst, inSecond))
			return;
		Long first = inFirst.get();
		Long second = inSecond.get();
		outValue.set(new RPIdouble((second - first) / 1000.0));
	}
}
