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
import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.io.javarcc.interfaces.JInputOutputInterface;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;

/**
 * Digital output sensor
 */
public class JOutBoolSensor extends JPrimitive {

	/** value currently set on output */
	final JOutPort<RPIbool> outIO = add("outIO", new JOutPort<RPIbool>());

	/** Device name IO is connected to */
	final JParameter<RPIstring> propDeviceID = add("DeviceID", new JParameter<RPIstring>(new RPIstring("")));

	/** Port to be used */
	final JParameter<RPIint> propPort = add("Port", new JParameter<RPIint>(new RPIint("0")));

	private JInputOutputInterface device;
	private RPIbool value = new RPIbool();

	@Override
	public Set<JDevice> getSensors() {
		return deviceSet(device);
	}

	@Override
	public void readSensor() {
		value.set(device.getDigitalOut(propPort.get().get()));
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
		device = device(propDeviceID, JInputOutputInterface.class);
		if (propPort.get().get() < 0 || propPort.get().get() >= device.getNumDigitalOut())
			throw new IllegalArgumentException("Port");
	}

	@Override
	public void updateData() {
		outIO.set(value);
	}

}