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
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;

/**
 * This class implements a rotation combiner module
 */
public class JRotationFromQuaternion extends JPrimitive {
	private JParameter<RPIdouble> propX = add("X", new JParameter<RPIdouble>()),
			propY = add("Y", new JParameter<RPIdouble>()), propZ = add("Z", new JParameter<RPIdouble>()),
			propW = add("W", new JParameter<RPIdouble>());
	private JInPort<RPIdouble> inX = add("inX", new JInPort<RPIdouble>()), inY = add("inY", new JInPort<RPIdouble>()),
			inZ = add("inZ", new JInPort<RPIdouble>()), inW = add("inW", new JInPort<RPIdouble>());
	private JOutPort<RPIRotation> outValue = add("outValue", new JOutPort<RPIRotation>());

	private MutableRotation rotation = RPICalc.rotationCreate();
	private RPIRotation value = RPICalc.rpiRotationCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		RPIdouble x = inX.get(propX);
		RPIdouble y = inY.get(propY);
		RPIdouble z = inZ.get(propZ);
		RPIdouble w = inW.get(propW);
		if (anyNull(x, y, z, w))
			return;
		rotation.setQuaternion(x.get(), y.get(), z.get(), w.get());
		RPICalc.rotationToRpi(rotation, value);
		outValue.set(value);
	}

};
