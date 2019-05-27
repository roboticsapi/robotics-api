/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.core.types.RPIdoubleArray;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.robot.javarcc.interfaces.JArmKinematicsInterface;
import org.roboticsapi.runtime.world.types.RPIFrame;

public class JInvKin extends JPrimitive {
	private JInPort<RPIFrame> inFrame = add("inFrame", new JInPort<RPIFrame>());
	private JInPort<RPIdoubleArray> inHintJoints = add("inHintJoints", new JInPort<RPIdoubleArray>());
	private JOutPort<RPIdoubleArray> outJoints = add("outJoints", new JOutPort<RPIdoubleArray>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JArmKinematicsInterface dev;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFrame, inHintJoints);
		dev = device(propRobot, JArmKinematicsInterface.class);
	}

	@Override
	public void updateData() {
		if (anyNull(inHintJoints, inFrame))
			return;
		double[] joints = new double[inHintJoints.get().getSize()];
		for (int i = 0; i < joints.length; i++)
			joints[i] = inHintJoints.get().get(i).get();
		double[] ret = dev.invKin(joints, inFrame.get());
		RPIdoubleArray arr = new RPIdoubleArray(joints.length);
		for (int i = 0; i < joints.length; i++) {
			arr.set(i, new RPIdouble(ret[i]));
		}
		outJoints.set(arr);
	}

}
