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
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIRotation;

/**
 * This class implements a rotation combiner module
 */
public class JRotationFromABC extends JPrimitive {
	private JInPort<RPIdouble> inA = add("inA", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inB = add("inB", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inC = add("inC", new JInPort<RPIdouble>());
	private JOutPort<RPIRotation> outValue = add("outValue", new JOutPort<RPIRotation>());
	private JParameter<RPIdouble> propA = add("A", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propB = add("B", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propC = add("C", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		RPIdouble a = inA.get(propA);
		RPIdouble b = inB.get(propB);
		RPIdouble c = inC.get(propC);
		if (anyNull(a, b, c))
			return;
		outValue.set(new RPIRotation(a, b, c));
	}
};
