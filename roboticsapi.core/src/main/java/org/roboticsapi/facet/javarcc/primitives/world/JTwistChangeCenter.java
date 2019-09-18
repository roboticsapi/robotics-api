/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableTwist;
import org.roboticsapi.core.world.mutable.MutableVector;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

/**
 * This class implements a wrench splitting module
 */
public class JTwistChangeCenter extends JPrimitive {
	private JInPort<RPITwist> inValue = add("inValue", new JInPort<RPITwist>());
	private JInPort<RPIVector> inVector = add("inVector", new JInPort<RPIVector>());
	private JOutPort<RPITwist> outValue = add("outValue", new JOutPort<RPITwist>());

	private RPITwist value = RPICalc.rpiTwistCreate();
	private MutableTwist twist = RPICalc.twistCreate();
	private MutableVector vector = RPICalc.vectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue, inVector);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue, inVector))
			return;
		RPICalc.rpiToTwist(inValue.get(), twist);
		RPICalc.rpiToVector(inVector.get(), vector);
		twist.getRotation().crossTo(vector, vector);
		twist.getTranslation().add(vector);
		RPICalc.twistToRpi(twist, value);
		outValue.set(value);
	}
};
