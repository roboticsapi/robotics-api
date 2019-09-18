/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableQuaternion;
import org.roboticsapi.core.world.mutable.MutableRotation;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;

/**
 * This class implements a rotation splitter module
 */
public class JRotationToQuaternion extends JPrimitive {
	private JInPort<RPIRotation> inValue = add("inValue", new JInPort<RPIRotation>());
	private JOutPort<RPIdouble> outX = add("outX", new JOutPort<RPIdouble>()),
			outY = add("outY", new JOutPort<RPIdouble>()), outZ = add("outZ", new JOutPort<RPIdouble>()),
			outW = add("outW", new JOutPort<RPIdouble>());

	private MutableQuaternion q = RPICalc.quaternionCreate();
	private MutableRotation rot = RPICalc.rotationCreate();
	private RPIdouble x = RPICalc.rpiDoubleCreate(), y = RPICalc.rpiDoubleCreate(), z = RPICalc.rpiDoubleCreate(),
			w = RPICalc.rpiDoubleCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIRotation rrot = inValue.get();
		RPICalc.rpiToRotation(rrot, rot);
		rot.getQuaternionTo(q);
		RPICalc.doubleToRpi(q.getX(), x);
		RPICalc.doubleToRpi(q.getY(), y);
		RPICalc.doubleToRpi(q.getZ(), z);
		RPICalc.doubleToRpi(q.getW(), w);
		outX.set(x);
		outY.set(y);
		outZ.set(z);
		outW.set(w);
	}

};
