/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.runtime.core.NetBasedCommandHandle;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.NetHandle;
import org.roboticsapi.runtime.world.PersistedVectorSensor;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.VectorInterNetcommIn;
import org.roboticsapi.runtime.world.result.VectorSensorMapperResult;
import org.roboticsapi.world.Vector;

public class PersistedVectorSensorMapper implements SensorMapper<AbstractMapperRuntime, Vector, PersistedVectorSensor> {

	@Override
	public SensorMapperResult<Vector> map(AbstractMapperRuntime runtime, PersistedVectorSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("PersistedVectorSensor");

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
		VectorInterNetcommIn netcomm = result.add(new VectorInterNetcommIn("", net, key));

		DataflowOutPort resultPort = result.addOutPort(new VectorDataflow(), false, netcomm.getOutValue());
		return new VectorSensorMapperResult(result, resultPort);
	}
}
