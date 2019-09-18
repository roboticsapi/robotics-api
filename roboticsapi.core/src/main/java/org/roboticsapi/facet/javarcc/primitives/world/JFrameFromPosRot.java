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
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

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