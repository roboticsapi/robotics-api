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
import org.roboticsapi.runtime.world.PersistedTransformationSensor;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameInterNetcommIn;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;

public class PersistedTransformationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, PersistedTransformationSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, PersistedTransformationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("PersistedTransformationSensor");

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
		FrameInterNetcommIn netcomm = result.add(new FrameInterNetcommIn("", net, key));

		DataflowOutPort resultPort = result.addOutPort(new TransformationDataflow(), false, netcomm.getOutValue());
		return new TransformationSensorMapperResult(result, resultPort);
	}
}
