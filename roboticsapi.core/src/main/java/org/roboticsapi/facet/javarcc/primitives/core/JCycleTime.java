/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JCycleTime extends JPrimitive {
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		outValue.set(new RPIdouble(getNet().getCycleTime()));
	}
}
