/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.runtime.core.NetBasedCommandHandle;
import org.roboticsapi.runtime.core.PersistedIntegerSensor;
import org.roboticsapi.runtime.core.primitives.IntInterNetcommIn;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.IntSensorMapperResult;
import org.roboticsapi.runtime.rpi.NetHandle;

public class PersistedIntegerSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Integer, PersistedIntegerSensor> {

	@Override
	public SensorMapperResult<Integer> map(AbstractMapperRuntime runtime, PersistedIntegerSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("PersistedIntegerSensor");

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
		IntInterNetcommIn netcomm = result.add(new IntInterNetcommIn("", net, key));

		DataflowOutPort resultPort = result.addOutPort(new IntDataflow(), false, netcomm.getOutValue());
		return new IntSensorMapperResult(result, resultPort);
	}
}
