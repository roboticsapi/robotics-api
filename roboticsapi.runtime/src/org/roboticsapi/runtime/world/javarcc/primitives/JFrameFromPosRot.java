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
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.runtime.world.types.RPIVector;

/**
 * This class implements a frame combiner module
 */
public class JFrameFromPosRot extends JPrimitive {
	private JInPort<RPIVector> inPos = add("inPos", new JInPort<RPIVector>());
	private JInPort<RPIRotation> inRot = add("inRot", new JInPort<RPIRotation>());
	private JOutPort<RPIFrame> outValue = add("outValue", new JOutPort<RPIFrame>());
	private JParameter<RPIdouble> propX = add("X", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propY = add("Y", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propZ = add("Z", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propA = add("A", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propB = add("B", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propC = add("C", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		RPIVector pos = inPos.get(new RPIVector(propX.get(), propY.get(), propZ.get()));
		RPIRotation rot = inRot.get(new RPIRotation(propA.get(), propB.get(), propC.get()));
		if (anyNull(pos, rot))
			return;
		outValue.set(new RPIFrame(pos, rot));
	}
};