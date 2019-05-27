/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIVector;

/**
 * This class implements a vector splitting module
 */
public class JVectorToXYZ extends JPrimitive {
	private JInPort<RPIVector> inValue = add("inValue", new JInPort<RPIVector>());
	private JOutPort<RPIdouble> outX = add("outX", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outY = add("outY", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outZ = add("outZ", new JOutPort<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIVector frame = inValue.get();
		outX.set(frame.getX());
		outY.set(frame.getY());
		outZ.set(frame.getZ());
	}
}
