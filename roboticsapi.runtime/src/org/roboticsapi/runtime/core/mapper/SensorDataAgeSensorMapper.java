/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.SensorDataAgeSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.TimeDiff;
import org.roboticsapi.runtime.core.primitives.TimeNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.TimestampDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class SensorDataAgeSensorMapper implements SensorMapper<SoftRobotRuntime, Double, SensorDataAgeSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, SensorDataAgeSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("SensorDataAge");
		SensorMapperResult<?> innerSensor = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor(), result,
				context);

		TimeNet timeNet = result.add(new TimeNet());
		TimeDiff timeDiff = result.add(new TimeDiff());
		result.connect(innerSensor.getSensorTimePort(), timeDiff.getInFirst(), new TimestampDataflow());
		result.connect(timeNet.getOutValue(), timeDiff.getInSecond());
		DataflowOutPort resultPort = result.addOutPort(new DoubleDataflow(), false, timeDiff.getOutValue());
		return new DoubleSensorMapperResult(result, resultPort);
	}

}
