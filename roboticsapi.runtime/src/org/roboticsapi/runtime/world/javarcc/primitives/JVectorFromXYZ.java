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
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements a vector combiner module
 */
public class JVectorFromXYZ extends JPrimitive {
	private JInPort<RPIdouble> inX = add("inX", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inY = add("inY", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inZ = add("inZ", new JInPort<RPIdouble>());
	private JOutPort<RPIVector> outValue = add("outValue", new JOutPort<RPIVector>());
	private JParameter<RPIdouble> propX = add("X", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propY = add("Y", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propZ = add("Z", new JParameter<RPIdouble>());

	private MutableVector vector = RPICalc.vectorCreate();
	private RPIVector value = RPICalc.rpiVectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		RPIdouble x = inX.get(propX.get());
		RPIdouble y = inY.get(propY.get());
		RPIdouble z = inZ.get(propZ.get());
		if (anyNull(x, y, z))
			return;
		vector.set(x.get(), y.get(), z.get());
		RPICalc.vectorToRpi(vector, value);
		outValue.set(value);
	}
};