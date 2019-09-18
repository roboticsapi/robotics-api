/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableVector;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

/**
 * This class implements a vector dot product module
 */
public class JVectorDot extends JPrimitive {
	private JInPort<RPIVector> inFirst = add("inFirst", new JInPort<RPIVector>());
	private JInPort<RPIVector> inSecond = add("inSecond", new JInPort<RPIVector>());
	private JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	RPIdouble value = RPICalc.rpiDoubleCreate();
	MutableVector first = RPICalc.vectorCreate();
	MutableVector second = RPICalc.vectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFirst, inSecond);
	}

	@Override
	public void updateData() {
		if (anyNull(inFirst, inSecond))
			return;
		RPICalc.rpiToVector(inFirst.get(), first);
		RPICalc.rpiToVector(inSecond.get(), second);
		RPICalc.doubleToRpi(first.dot(second), value);
		outValue.set(value);
	}
};
