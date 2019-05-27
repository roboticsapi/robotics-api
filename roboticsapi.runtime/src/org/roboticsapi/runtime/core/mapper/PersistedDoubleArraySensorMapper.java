/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.runtime.core.NetBasedCommandHandle;
import org.roboticsapi.runtime.core.PersistedDoubleArraySensor;
import org.roboticsapi.runtime.core.primitives.DoubleArrayInterNetcommIn;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleArrayDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleArraySensorMapperResult;
import org.roboticsapi.runtime.rpi.NetHandle;

public class PersistedDoubleArraySensorMapper
		implements SensorMapper<AbstractMapperRuntime, Double[], PersistedDoubleArraySensor> {

	@Override
	public SensorMapperResult<Double[]> map(AbstractMapperRuntime runtime, PersistedDoubleArraySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("PersistedDoubleArraySensor");

		String key = sensor.getKey().getName();
		String net = "";
		CommandHandle handle = sensor.getCommand().getCommandHandle();
		if (handle instanceof NetBasedCommandHandle) {
			NetBasedCommandHandle softrobotCommandHandle = (NetBasedCommandHandle) handle;
			NetHandle netHandle = softrobotCommandHandle.getNetHandle();
			net = netHandle.getName();
		}
		if (net.isEmpty()) {
			throw new MappingException("Source net not found.");
		}
		DoubleArrayInterNetcommIn netcomm = result.add(new DoubleArrayInterNetcommIn("", net, key));

		DataflowOutPort resultPort = result.addOutPort(new DoubleArrayDataflow(sensor.getSize()), false,
				netcomm.getOutValue());
		return new DoubleArraySensorMapperResult(result, resultPort);
	}
}
