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
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

/**
 * This class implements a frame splitting module
 */
public class JFrameToPosRot extends JPrimitive {
	private JInPort<RPIFrame> inValue = add("inValue", new JInPort<RPIFrame>());
	private JOutPort<RPIVector> outPosition = add("outPosition", new JOutPort<RPIVector>());
	private JOutPort<RPIRotation> outRotation = add("outRotation", new JOutPort<RPIRotation>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIFrame frame = inValue.get();
		outPosition.set(frame.getPos());
		outRotation.set(frame.getRot());
	}

};
