/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableRotation;
import org.roboticsapi.core.world.mutable.MutableVector;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

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
