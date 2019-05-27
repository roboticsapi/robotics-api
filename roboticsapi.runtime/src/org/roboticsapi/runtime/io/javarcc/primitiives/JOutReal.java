/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.javarcc.primitiives;

import java.util.Set;

import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.io.javarcc.interfaces.JInputOutputInterface;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;

/**
 * Analog output
 */
public class JOutReal extends JPrimitive {

	/** Activation port */
	final JInPort<RPIbool> inActive = add("inActive", new JInPort<RPIbool>());

	/** Value to set on output */
	final JInPort<RPIdouble> inIO = add("inIO", new JInPort<RPIdouble>());

	/** Device name IO is connected to */
	final JParameter<RPIstring> propDeviceID = add("DeviceID", new JParameter<RPIstring>(new RPIstring("")));

	/** Port to be used */
	final JParameter<RPIint> propPort = add("Port", new JParameter<RPIint>(new RPIint("0")));

	/** Value to set to output when inIO is not connected */
	final JParameter<RPIdouble> propIO = add("IO", new JParameter<RPIdouble>(new RPIdouble("0")));

	private JInputOutputInterface device;
	private double value = 0;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		device = device(propDeviceID, JInputOutputInterface.class);
		if (propPort.get().get() < 0 || propPort.get().get() >= device.getNumAnalogOut())
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
		device.setAnalogOut(propPort.get().get(), value);
	}

}