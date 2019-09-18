/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.javarcc.primitives;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.primitives.world.RPICalc;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.framework.robot.javarcc.interfaces.JArmKinematicsInterface;

public class JKin extends JPrimitive {
	private JInPort<RPIdoubleArray> inJoints = add("inJoints", new JInPort<RPIdoubleArray>());
	private JOutPort<RPIFrame> outFrame = add("outFrame", new JOutPort<RPIFrame>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JArmKinematicsInterface dev;
	private RPIFrame frame = RPICalc.rpiFrameCreate();

	public void checkParameters() throws IllegalArgumentException {
		connected(inJoints);
		dev = device(propRobot, JArmKinematicsInterface.class);
	};

	@Override
	public void updateData() {
		if (anyNull(inJoints))
			return;
		double[] joints = new double[inJoints.get().getSize()];
		for (int i = 0; i < joints.length; i++)
			joints[i] = inJoints.get().get(i).get();
		outFrame.set(dev.kin(joints, frame));
	}
}
