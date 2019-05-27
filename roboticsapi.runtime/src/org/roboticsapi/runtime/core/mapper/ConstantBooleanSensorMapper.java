/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.ConstantBooleanSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;

public class ConstantBooleanSensorMapper implements SensorMapper<SoftRobotRuntime, Boolean, ConstantBooleanSensor> {

	@Override
	public SensorMapperResult<Boolean> map(SoftRobotRuntime runtime, ConstantBooleanSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Constant Boolean");
		NetFragment val = new NetFragment("Value");
		BooleanValue value = val.add(new BooleanValue(sensor.getValue()));
		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new StateDataflow(), false, value.getOutValue());
		return new BooleanSensorMapperResult(result, resultPort);
	}
}
