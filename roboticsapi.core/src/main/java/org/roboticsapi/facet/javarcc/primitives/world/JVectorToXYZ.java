/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

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
