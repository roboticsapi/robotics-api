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

/**
 * This class implements a rotation combiner module
 */
public class JRotationToABC extends JPrimitive {
	private JOutPort<RPIdouble> outA = add("outA", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outB = add("outB", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outC = add("outC", new JOutPort<RPIdouble>());
	private JInPort<RPIRotation> inValue = add("inValue", new JInPort<RPIRotation>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIRotation rotation = inValue.get();
		outA.set(rotation.getA());
		outB.set(rotation.getB());
		outC.set(rotation.getC());
	}
};