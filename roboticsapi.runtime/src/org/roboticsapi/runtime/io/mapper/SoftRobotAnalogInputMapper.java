/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.mapper;

import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.io.SoftRobotAnalogInputDriver;
import org.roboticsapi.runtime.io.primitives.InReal;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class SoftRobotAnalogInputMapper
		implements SensorMapper<SoftRobotRuntime, Double, SoftRobotAnalogInputDriver.AnalogInputValue> {

	@Override
	public DoubleSensorMapperResult map(final SoftRobotRuntime runtime,
			final SoftRobotAnalogInputDriver.AnalogInputValue sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {
		final int number = sensor.getDriver().getNumber();
		final String friName = sensor.getDriver().getDeviceName();

		NetFragment net = new NetFragment("SoftRobotAnalogInput");
		final NetFragment inputNet = new NetFragment("Value");
		net.add(inputNet);
		final InReal inputPrimitive = new InReal(friName, number);
		inputNet.add(inputPrimitive);
		DataflowOutPort valuePort = inputNet.addOutPort(new DoubleDataflow(), true, inputPrimitive.getOutIO());
		return new DoubleSensorMapperResult(net, valuePort);
	}

}
