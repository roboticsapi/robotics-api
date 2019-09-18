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
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

/**
 * This class implements a vector scale module
 */
public class JVectorScale extends JPrimitive {
	private JInPort<RPIVector> inValue = add("inValue", new JInPort<RPIVector>());
	private JInPort<RPIdouble> inFactor = add("inFactor", new JInPort<RPIdouble>());
	private JOutPort<RPIVector> outValue = add("outValue", new JOutPort<RPIVector>());
	private JParameter<RPIdouble> propFactor = add("Factor", new JParameter<RPIdouble>());

	private MutableVector vector = RPICalc.vectorCreate();
	private RPIVector value = RPICalc.rpiVectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		RPIdouble factor = inFactor.get(propFactor);
		if (anyNull(inValue.get(), factor))
			return;
		RPICalc.rpiToVector(inValue.get(), vector);
		vector.scale(factor.get());
		RPICalc.vectorToRpi(vector, value);
		outValue.set(value);
	}

};
