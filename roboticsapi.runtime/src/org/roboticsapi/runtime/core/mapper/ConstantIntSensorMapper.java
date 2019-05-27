/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.ConstantIntegerSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.IntValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.IntSensorMapperResult;

public class ConstantIntSensorMapper implements SensorMapper<SoftRobotRuntime, Integer, ConstantIntegerSensor> {

	@Override
	public SensorMapperResult<Integer> map(SoftRobotRuntime runtime, ConstantIntegerSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Constant double");
		NetFragment val = new NetFragment("Value");
		IntValue value = val.add(new IntValue(sensor.getValue()));
		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new IntDataflow(), false, value.getOutValue());
		return new IntSensorMapperResult(result, resultPort);
	}
}
