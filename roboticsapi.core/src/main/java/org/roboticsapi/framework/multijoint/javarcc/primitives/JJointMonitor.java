/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JMultijointInterface;

public class JJointMonitor extends JPrimitive {
	private JOutPort<RPIdouble> outCmdPos = add("outCommandedPosition", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outMsrPos = add("outMeasuredPosition", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outCmdVel = add("outCommandedVelocity", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outMsrVel = add("outMeasuredVelocity", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outCmdAcc = add("outCommandedAcceleration", new JOutPort<RPIdouble>());
	private JOutPort<RPIdouble> outMsrAcc = add("outMeasuredAcceleration", new JOutPort<RPIdouble>());
	private JOutPort<RPIint> outError = add("outError", new JOutPort<RPIint>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JParameter<RPIint> propAxis = add("Axis", new JParameter<RPIint>());

	private JMultijointInterface dev;
	private double cmdpos, cmdvel, cmdacc, msrpos, msrvel, msracc;
	private int error;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		dev = device(propRobot, JMultijointInterface.class);
		if (propAxis.get().get() < 0 || propAxis.get().get() >= dev.getJointCount())
			throw new IllegalArgumentException("Axis");
	}

	@Override
	public Set<JDevice> getSensors() {
		return deviceSet(dev);
	}

	@Override
	public void readSensor() {
		int axis = propAxis.get().get();
		cmdpos = dev.getCommandedJointPosition(axis);
		cmdvel = dev.getCommandedJointVelocity(axis);
		cmdacc = dev.getCommandedJointAcceleration(axis);
		msrpos = dev.getMeasuredJointPosition(axis);
		msrvel = dev.getMeasuredJointVelocity(axis);
		msracc = dev.getMeasuredJointAcceleration(axis);
		error = dev.getJointError(axis);
	}

	@Override
	public void updateData() {
		outCmdAcc.set(new RPIdouble(cmdacc));
		outMsrAcc.set(new RPIdouble(msracc));
		outCmdVel.set(new RPIdouble(cmdvel));
		outMsrVel.set(new RPIdouble(msrvel));
		outCmdPos.set(new RPIdouble(cmdpos));
		outMsrPos.set(new RPIdouble(msrpos));
		outError.set(new RPIint(error));
	}
}
