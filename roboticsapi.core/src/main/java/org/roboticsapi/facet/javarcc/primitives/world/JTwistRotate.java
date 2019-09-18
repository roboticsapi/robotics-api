/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableRotation;
import org.roboticsapi.core.world.mutable.MutableTwist;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;

/**
 * This class implements a twist rotation module
 */
public class JTwistRotate extends JPrimitive {
	private JInPort<RPITwist> inValue = add("inValue", new JInPort<RPITwist>());
	private JInPort<RPIRotation> inRot = add("inRot", new JInPort<RPIRotation>());
	private JOutPort<RPITwist> outValue = add("outValue", new JOutPort<RPITwist>());

	private MutableRotation rot = RPICalc.rotationCreate();
	private MutableTwist twist = RPICalc.twistCreate();
	private RPITwist value = RPICalc.rpiTwistCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue, inRot);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue, inRot))
			return;
		RPICalc.rpiToTwist(inValue.get(), twist);
		RPICalc.rpiToRotation(inRot.get(), rot);
		twist.getTranslation().rotate(rot);
		twist.getRotation().rotate(rot);
		RPICalc.twistToRpi(twist, value);
		outValue.set(value);
	}

};
