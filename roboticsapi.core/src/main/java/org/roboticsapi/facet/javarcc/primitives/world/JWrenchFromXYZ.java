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
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIWrench;

/**
 * This class implements a wrench combiner module
 */
public class JWrenchFromXYZ extends JPrimitive {
	private JInPort<RPIdouble> inX = add("inX", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inY = add("inY", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inZ = add("inZ", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inA = add("inA", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inB = add("inB", new JInPort<RPIdouble>());
	private JInPort<RPIdouble> inC = add("inC", new JInPort<RPIdouble>());
	private JOutPort<RPIWrench> outValue = add("outValue", new JOutPort<RPIWrench>());
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
		RPIdouble x = inX.get(propX);
		RPIdouble y = inY.get(propY);
		RPIdouble z = inZ.get(propZ);
		RPIdouble a = inA.get(propA);
		RPIdouble b = inB.get(propB);
		RPIdouble c = inC.get(propC);
		if (anyNull(x, y, z, a, b, c))
			return;
		RPIVector force = new RPIVector(x, y, z);
		RPIVector torque = new RPIVector(c, b, a);
		outValue.set(new RPIWrench(force, torque));
	}
};
