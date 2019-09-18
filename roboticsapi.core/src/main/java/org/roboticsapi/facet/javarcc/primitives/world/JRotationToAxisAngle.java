/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableRotation;
import org.roboticsapi.core.world.mutable.MutableVector;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

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
