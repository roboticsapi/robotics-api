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
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameTransform;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.MultipliedTransformationSensor;

public class MultipliedTransformationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, MultipliedTransformationSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, MultipliedTransformationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Multiplied Transformation");
		SensorMapperResult<Transformation> firstResult = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getFirstTransformation(), result, context);
		SensorMapperResult<Transformation> secondResult = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getSecondTransformation(), result, context);

		NetFragment inv = new NetFragment("Multiply");
		FrameTransform invert = inv.add(new FrameTransform());
		inv.connect(firstResult.getSensorPort(),
				inv.addInPort(firstResult.getSensorPort().getType(), true, invert.getInFirst()));
		inv.connect(secondResult.getSensorPort(),
				inv.addInPort(secondResult.getSensorPort().getType(), true, invert.getInSecond()));
		result.add(inv);

		DataflowOutPort resultPort = inv.addOutPort(new TransformationDataflow(), false, invert.getOutValue());
		return new TransformationSensorMapperResult(result, resultPort);
	}
}
