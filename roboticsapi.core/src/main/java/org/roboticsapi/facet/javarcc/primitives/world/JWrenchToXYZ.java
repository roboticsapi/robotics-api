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
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIWrench;

/**
 * This class implements a wrench splitting module
 */
public class JWrenchToXYZ extends JPrimitive {
	private JInPort<RPIWrench> inValue = add("inValue", new JInPort<RPIWrench>());
	private JOutPort<RPIdouble> outX = add("outX", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outY = add("outY", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outZ = add("outZ", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outA = add("outA", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outB = add("outB", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outC = add("outC", new JOutPort<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIWrench wrench = inValue.get();
		outX.set(wrench.getForce().getX());
		outY.set(wrench.getForce().getY());
		outZ.set(wrench.getForce().getZ());
		outA.set(wrench.getTorque().getZ());
		outB.set(wrench.getTorque().getY());
		outC.set(wrench.getTorque().getX());
	}
};
