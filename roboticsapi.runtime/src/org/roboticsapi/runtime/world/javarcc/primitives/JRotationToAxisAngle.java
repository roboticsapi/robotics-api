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
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.mutable.MutableRotation;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements a rotation splitter module
 */
public class JRotationToAxisAngle extends JPrimitive {
	private JInPort<RPIRotation> inValue = add("inValue", new JInPort<RPIRotation>());
	private JOutPort<RPIVector> outAxis = add("outAxis", new JOutPort<RPIVector>());
	private JOutPort<RPIdouble> outAngle = add("outAngle", new JOutPort<RPIdouble>());

	RPIVector rAxis = RPICalc.rpiVectorCreate();
	RPIdouble rAngle = RPICalc.rpiDoubleCreate();
	MutableRotation rot = RPICalc.rotationCreate();
	MutableVector axis = RPICalc.vectorCreate();

	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIRotation r = inValue.get();
		RPICalc.rpiToRotation(r, rot);
		rot.getUnitAxisTo(axis);
		RPICalc.doubleToRpi(rot.getAngle(), rAngle);
		RPICalc.vectorToRpi(axis, rAxis);
		outAngle.set(rAngle);
		outAxis.set(rAxis);
	}

};
