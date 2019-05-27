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
import org.roboticsapi.world.mutable.MutableRotation;

/**
 * This class implements a rotation combiner module
 */
public class JRotationTransform extends JPrimitive {
	private JInPort<RPIRotation> inFirst = add("inFirst", new JInPort<RPIRotation>());
	private JInPort<RPIRotation> inSecond = add("inSecond", new JInPort<RPIRotation>());
	private JOutPort<RPIRotation> outValue = add("outValue", new JOutPort<RPIRotation>());

	private MutableRotation first = RPICalc.rotationCreate();
	private MutableRotation second = RPICalc.rotationCreate();
	private RPIRotation value = RPICalc.rpiRotationCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFirst, inSecond);
	}

	@Override
	public void updateData() {
		if (anyNull(inFirst, inSecond))
			return;
		RPICalc.rpiToRotation(inFirst.get(), first);
		RPICalc.rpiToRotation(inSecond.get(), second);
		first.multiply(second);
		RPICalc.rotationToRpi(first, value);
		outValue.set(value);
	}

};
