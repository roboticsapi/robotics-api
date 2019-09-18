/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;

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
