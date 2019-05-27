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
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.mutable.MutableRotation;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements a rotation combiner module
 */
public class JRotationFromAxisAngle extends JPrimitive {
	private JInPort<RPIVector> inAxis = add("inAxis", new JInPort<RPIVector>());
	private JInPort<RPIdouble> inAngle = add("inAngle", new JInPort<RPIdouble>());
	private JOutPort<RPIRotation> outValue = add("outValue", new JOutPort<RPIRotation>());
	private JParameter<RPIdouble> propX = add("X", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propY = add("Y", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propZ = add("Z", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propAngle = add("Angle", new JParameter<RPIdouble>());

	private RPIRotation value = RPICalc.rpiRotationCreate();
	private MutableRotation rotation = RPICalc.rotationCreate();
	private MutableVector vector = RPICalc.vectorCreate();
	private RPIVector propAxis = null;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		propAxis = new RPIVector(propX.get(), propY.get(), propZ.get());
	}

	@Override
	public void updateData() {
		RPIVector axis = inAxis.get(propAxis);
		RPIdouble angle = inAngle.get(propAngle);
		if (anyNull(axis, angle))
			return;
		RPICalc.rpiToVector(axis, vector);
		vector.scale(angle.get());
		rotation.setAxis(vector);
		RPICalc.rotationToRpi(rotation, value);
		outValue.set(value);
	}
};
