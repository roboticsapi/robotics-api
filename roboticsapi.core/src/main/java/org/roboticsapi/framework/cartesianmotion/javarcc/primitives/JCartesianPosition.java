/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.framework.cartesianmotion.javarcc.interfaces.JCartesianPositionInterface;

public class JCartesianPosition extends JPrimitive {
	private JInPort<RPIFrame> inPosition = add("inPosition", new JInPort<RPIFrame>());
	private JOutPort<RPIint> outError = add("outError", new JOutPort<RPIint>());
	private JOutPort<RPIbool> outErrorConcurrentAccess = add("outErrorConcurrentAccess", new JOutPort<RPIbool>());
	private JOutPort<RPIbool> outErrorDeviceFailed = add("outErrorDeviceFailed", new JOutPort<RPIbool>());
	private JOutPort<RPIbool> outErrorIllegalPosition = add("outErrorIllegalPosition", new JOutPort<RPIbool>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JCartesianPositionInterface dev;

	private int error;
	private boolean hasValue;
	private RPIFrame pos;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inPosition);
		dev = device(propRobot, JCartesianPositionInterface.class);
	}

	@Override
	public void updateData() {
		if (anyNull(inPosition))
			return;
		pos = inPosition.get();
		int deviceError = dev.getCartesianPositionDeviceError();
		int positionError = dev.checkPosition(pos);

		outErrorConcurrentAccess.set(new RPIbool(false));
		outErrorDeviceFailed.set(new RPIbool(deviceError != 0));
		outErrorIllegalPosition.set(new RPIbool(positionError != 0));
		hasValue = false;

		if (deviceError != 0) {
			error = deviceError;
		} else if (positionError != 0) {
			error = positionError;
		} else {
			error = 0;
			hasValue = true;
		}
		outError.set(new RPIint(error));
	}

	@Override
	public void writeActuator() {
		if (hasValue) {
			dev.setPosition(pos, getNet().getTime());
			hasValue = false;
		}
	}

	@Override
	public Set<JDevice> getActuators() {
		return deviceSet(dev);
	}

}
