/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives;

import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;

public class JCancel extends JPrimitive {
	private JOutPort<RPIbool> outCancel = add("outCancel", new JOutPort<RPIbool>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	public JOutPort<RPIbool> getOutCancel() {
		return outCancel;
	}

	@Override
	public void updateData() {
		if (getNet().isCancelled()) {
			outCancel.set(new RPIbool(true));
		} else {
			outCancel.set(new RPIbool(false));
		}
	}

}
