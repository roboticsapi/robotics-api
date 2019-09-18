/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableRotation;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;

/**
 * This class implements a rotation splitter module
 */
public class JRotationInvert extends JPrimitive {
	private JInPort<RPIRotation> inValue = add("inValue", new JInPort<RPIRotation>());
	private JOutPort<RPIRotation> outValue = add("outValue", new JOutPort<RPIRotation>());

	private MutableRotation rotation = RPICalc.rotationCreate();
	private RPIRotation value = RPICalc.rpiRotationCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIRotation rot = inValue.get();
		RPICalc.rpiToRotation(rot, rotation);
		rotation.invert();
		RPICalc.rotationToRpi(rotation, value);
		outValue.set(value);
	}
};
