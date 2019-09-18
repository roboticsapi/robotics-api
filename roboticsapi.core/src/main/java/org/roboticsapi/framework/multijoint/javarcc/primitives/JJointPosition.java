/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JMultijointInterface;

public class JJointPosition extends JPrimitive {
	private JInPort<RPIdouble> inPosition = add("inPosition", new JInPort<RPIdouble>());
	private JOutPort<RPIbool> outErrorConcurrentAccess = add("outErrorConcurrentAccess", new JOutPort<RPIbool>());
	private JOutPort<RPIbool> outErrorJointFailed = add("outErrorJointFailed", new JOutPort<RPIbool>());
	private JOutPort<RPIbool> outErrorIllegalPosition = add("outErrorIllegalPosition", new JOutPort<RPIbool>());
	private JOutPort<RPIint> outError = add("outError", new JOutPort<RPIint>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JParameter<RPIint> propAxis = add("Axis", new JParameter<RPIint>());

	private JMultijointInterface dev;
	private Long time;
	private double jointPos;
	private boolean hasValues = false;
	private int axis;
	private int jointError;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inPosition);
		dev = device(propRobot, JMultijointInterface.class);
		axis = propAxis.get().get();
		if (axis < 0 || axis >= dev.getJointCount())
			throw new IllegalArgumentException("Axis");
	}

	public Set<JDevice> getSensors() {
		return deviceSet(dev);
	}

	@Override
	public void readSensor() {
		jointError = dev.getJointError(axis);
	}

	@Override
	public void updateData() {
		if (anyNull(inPosition))
			return;
		time = getNet().getTime();
		jointPos = inPosition.get().get();
		hasValues = false;
		int positionError = dev.checkJointPosition(axis, jointPos);
		outErrorJointFailed.set(new RPIbool(jointError != 0));
		outErrorConcurrentAccess.set(new RPIbool(false));
		outErrorIllegalPosition.set(new RPIbool(positionError != 0));
		if (jointError != 0) {
			outError.set(new RPIint(jointError));
		} else if (positionError != 0) {
			outError.set(new RPIint(positionError));
		} else {
			hasValues = true;
			outError.set(new RPIint(0));
		}
	}

	@Override
	public void writeActuator() {
		if (hasValues) {
			dev.setJointPosition(axis, jointPos, time);
			hasValues = false;
		}
	}

	@Override
	public Set<JDevice> getActuators() {
		return deviceSet(dev);
	}

}
