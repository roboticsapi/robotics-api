/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.platform.javarcc.interfaces.JRobotBaseInterface;

public class JWheelMonitor extends JPrimitive {
	private JOutPort<RPIdouble> outPos = add("outPos", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outVel = add("outVel", new JOutPort<RPIdouble>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JParameter<RPIint> propWheel = add("Wheel", new JParameter<RPIint>());

	private JRobotBaseInterface dev;
	private double pos;
	private double vel;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		dev = device(propRobot, JRobotBaseInterface.class);
		if (propWheel.get().get() < 0 || propWheel.get().get() > dev.getWheelCount()) {
			throw new IllegalArgumentException("Wheel");
		}
	}

	@Override
	public Set<JDevice> getSensors() {
		return deviceSet(dev);
	}

	@Override
	public void readSensor() {
		pos = dev.getWheelPosition(propWheel.get().get());
		vel = dev.getWheelVelocity(propWheel.get().get());
	}

	@Override
	public void updateData() {
		outPos.set(new RPIdouble(pos));
		outVel.set(new RPIdouble(vel));
	}

}
