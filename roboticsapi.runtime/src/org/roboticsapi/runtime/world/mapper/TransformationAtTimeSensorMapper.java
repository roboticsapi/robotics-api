/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameAtTime;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.TransformationAtTimeSensor;

public class TransformationAtTimeSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, TransformationAtTimeSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, TransformationAtTimeSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("TransformationAtTime");
		SensorMapperResult<Transformation> innerResult = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), result, context);
		SensorMapperResult<Double> ageResult = runtime.getMapperRegistry().mapSensor(runtime, sensor.getAge(), result,
				context);

		FrameAtTime not = result.add(new FrameAtTime(sensor.getMaxAge()));
		result.connect(innerResult.getSensorPort(),
				result.addInPort(innerResult.getSensorPort().getType(), true, not.getInValue()));
		result.connect(ageResult.getSensorPort(), not.getInAge(), new DoubleDataflow());

		DataflowOutPort resultPort = result.addOutPort(new TransformationDataflow(), false, not.getOutValue());
		return new TransformationSensorMapperResult(result, resultPort);
	}
}
