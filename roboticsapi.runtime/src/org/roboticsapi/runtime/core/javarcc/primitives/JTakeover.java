/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JTakeover extends JPrimitive {
	private JOutPort<RPIbool> outTakeover = add("outTakeover", new JOutPort<RPIbool>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	public JOutPort<RPIbool> getOutTakeover() {
		return outTakeover;
	}

	@Override
	public void updateData() {
		if (getNet().hasSuccessor()) {
			outTakeover.set(new RPIbool(true));
		} else {
			outTakeover.set(new RPIbool(false));
		}
	}

}
