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
import org.roboticsapi.runtime.world.primitives.FrameInvert;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.InvertedTransformationSensor;

public class InvertedTransformationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, InvertedTransformationSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, InvertedTransformationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Inverted Transformation");
		SensorMapperResult<Transformation> innerResult = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), result, context);
		NetFragment inv = new NetFragment("Invert");
		FrameInvert invert = inv.add(new FrameInvert());
		inv.connect(innerResult.getSensorPort(),
				inv.addInPort(innerResult.getSensorPort().getType(), true, invert.getInValue()));
		result.add(inv);
		DataflowOutPort resultPort = inv.addOutPort(new TransformationDataflow(), false, invert.getOutValue());
		return new TransformationSensorMapperResult(result, resultPort, innerResult.getSensorTimePort());
	}
}
