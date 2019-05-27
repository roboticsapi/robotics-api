/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.mutable.MutableRotation;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements a twist rotation module
 */
public class JVectorRotate extends JPrimitive {
	private JInPort<RPIVector> inValue = add("inValue", new JInPort<RPIVector>());
	private JInPort<RPIRotation> inRot = add("inRot", new JInPort<RPIRotation>());
	private JOutPort<RPIVector> outValue = add("outValue", new JOutPort<RPIVector>());

	private MutableRotation rot = RPICalc.rotationCreate();
	private MutableVector vector = RPICalc.vectorCreate();
	private RPIVector value = RPICalc.rpiVectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue, inRot);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue, inRot))
			return;
		RPICalc.rpiToVector(inValue.get(), vector);
		RPICalc.rpiToRotation(inRot.get(), rot);
		vector.rotate(rot);
		RPICalc.vectorToRpi(vector, value);
		outValue.set(value);
	}
};
