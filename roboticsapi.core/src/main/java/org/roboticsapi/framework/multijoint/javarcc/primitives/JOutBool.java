/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JInputOutputInterface;

/**
 * Digital output
 */
public class JOutBool extends JPrimitive {

	/** Activation port */
	final JInPort<RPIbool> inActive = add("inActive", new JInPort<RPIbool>());

	/** Value to set on output */
	final JInPort<RPIbool> inIO = add("inIO", new JInPort<RPIbool>());

	/** Device name IO is connected to */
	final JParameter<RPIstring> propDeviceID = add("DeviceID", new JParameter<RPIstring>(new RPIstring("")));

	/** Port to be used */
	final JParameter<RPIint> propPort = add("Port", new JParameter<RPIint>(new RPIint("0")));

	/** Value to set to output when inIO is not connected */
	final JParameter<RPIbool> propIO = add("IO", new JParameter<RPIbool>(new RPIbool("false")));

	private JInputOutputInterface device;
	private boolean value = false;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		device = device(propDeviceID, JInputOutputInterface.class);
		if (propPort.get().get() < 0 || propPort.get().get() >= device.getNumDigitalOut())
			throw new IllegalArgumentException("Port");
	}

	@Override
	public void updateData() {
		value = inIO.get().get();
	}

	@Override
	public Set<JDevice> getActuators() {
		return deviceSet(device);
	}

	@Override
	public void writeActuator() {
		device.setDigitalOut(propPort.get().get(), value);
	}

}