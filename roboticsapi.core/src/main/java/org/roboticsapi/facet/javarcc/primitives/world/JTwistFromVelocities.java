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
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

/**
 * This class implements a wrench combiner module
 */
public class JTwistFromVelocities extends JPrimitive {
	private JInPort<RPIVector> inTransVel = add("inTransVel", new JInPort<RPIVector>());
	private JInPort<RPIVector> inRotVel = add("inRotVel", new JInPort<RPIVector>());
	private JOutPort<RPITwist> outValue = add("outValue", new JOutPort<RPITwist>());
	private JParameter<RPIdouble> propX = add("X", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propY = add("Y", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propZ = add("Z", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propRX = add("RX", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propRY = add("RY", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> propRZ = add("RZ", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		RPIVector vel = inTransVel.get(new RPIVector(propX.get(), propY.get(), propZ.get()));
		RPIVector rot = inRotVel.get(new RPIVector(propRX.get(), propRY.get(), propRZ.get()));
		if (anyNull(vel, rot))
			return;
		outValue.set(new RPITwist(vel, rot));
	}

};
