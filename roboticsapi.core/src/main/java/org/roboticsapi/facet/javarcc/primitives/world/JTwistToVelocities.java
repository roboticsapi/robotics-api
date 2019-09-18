/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

/**
 * This class implements a wrench splitting module
 */
public class JTwistToVelocities extends JPrimitive {
	private JInPort<RPITwist> inValue = add("inValue", new JInPort<RPITwist>());
	private JOutPort<RPIVector> outTransVel = add("outTransVel", new JOutPort<RPIVector>());
	private JOutPort<RPIVector> outRotVel = add("outRotVel", new JOutPort<RPIVector>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPITwist twist = inValue.get();
		outTransVel.set(twist.getVel());
		outRotVel.set(twist.getRot());
	}

};
