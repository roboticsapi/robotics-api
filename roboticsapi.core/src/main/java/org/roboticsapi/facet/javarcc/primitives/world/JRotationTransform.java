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
