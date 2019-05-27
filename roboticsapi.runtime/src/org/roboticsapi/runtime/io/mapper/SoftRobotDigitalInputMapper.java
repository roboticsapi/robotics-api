/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.mapper;

import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.io.SoftRobotDigitalInputDriver;
import org.roboticsapi.runtime.io.primitives.InBool;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;

public class SoftRobotDigitalInputMapper
		implements SensorMapper<SoftRobotRuntime, Boolean, SoftRobotDigitalInputDriver.DigitalInputValue> {

	@Override
	public SensorMapperResult<Boolean> map(final SoftRobotRuntime runtime,
			final SoftRobotDigitalInputDriver.DigitalInputValue sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {
		SoftRobotDigitalInputDriver input = sensor.getParent();
		NetFragment net = new NetFragment("SoftRobotDigitalInput");
		InBool inputPrimitive = net.add(new InBool(input.getDeviceName(), input.getNumber()));
		// TODO: Change to StateDataflow again and fix I/O and gripper
		// problems...
		DataflowOutPort valuePort = net.addOutPort(new StateDataflow(), true, inputPrimitive.getOutIO());
		return new BooleanSensorMapperResult(net, valuePort);
	}

}
