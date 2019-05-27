/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.runtime.cartesianmotion.javarcc.interfaces.JCartesianPositionInterface;
import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPITwist;

public class JCartesianMonitor extends JPrimitive {
	private JOutPort<RPIFrame> outMsrPos = add("outMeasuredPosition", new JOutPort<RPIFrame>());
	private JOutPort<RPITwist> outMsrVel = add("outMeasuredVelocity", new JOutPort<RPITwist>());
	private JOutPort<RPIFrame> outCmdPos = add("outCommandedPosition", new JOutPort<RPIFrame>());
	private JOutPort<RPITwist> outCmdVel = add("outCommandedVelocity", new JOutPort<RPITwist>());
	private JOutPort<RPIint> outError = add("outError", new JOutPort<RPIint>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JCartesianPositionInterface dev;

	private RPIFrame pos;
	private RPITwist vel;
	private RPIFrame cmdpos;
	private RPITwist cmdvel;
	int error;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		dev = device(propRobot, JCartesianPositionInterface.class);
	}

	@Override
	public Set<JDevice> getSensors() {
		return deviceSet(dev);
	}

	@Override
	public void readSensor() {
		pos = dev.getMeasuredPosition();
		vel = dev.getMeasuredVelocity();
		cmdvel = dev.getCommandedVelocity();
		cmdpos = dev.getCommandedPosition();
		error = dev.getCartesianPositionDeviceError();
	}

	@Override
	public void updateData() {
		outMsrPos.set(pos);
		outMsrVel.set(vel);
		outCmdPos.set(cmdpos);
		outCmdVel.set(cmdvel);
		outError.set(new RPIint(error));
	}

}
