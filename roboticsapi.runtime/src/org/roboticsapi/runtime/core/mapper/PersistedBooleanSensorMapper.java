/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.runtime.core.NetBasedCommandHandle;
import org.roboticsapi.runtime.core.PersistedBooleanSensor;
import org.roboticsapi.runtime.core.primitives.BooleanInterNetcommIn;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;
import org.roboticsapi.runtime.rpi.NetHandle;

public class PersistedBooleanSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Boolean, PersistedBooleanSensor> {

	@Override
	public SensorMapperResult<Boolean> map(AbstractMapperRuntime runtime, PersistedBooleanSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("PersistedBooleanSensor");

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
		BooleanInterNetcommIn netcomm = result.add(new BooleanInterNetcommIn("", net, key));

		DataflowOutPort resultPort = result.addOutPort(new StateDataflow(), false, netcomm.getOutValue());
		return new BooleanSensorMapperResult(result, resultPort);
	}
}
