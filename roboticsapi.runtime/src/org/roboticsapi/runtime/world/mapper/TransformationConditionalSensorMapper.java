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
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameConditional;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.TransformationConditionalSensor;

public class TransformationConditionalSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, TransformationConditionalSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, TransformationConditionalSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("DoubleConditional");
		SensorMapperResult<Boolean> condition = runtime.getMapperRegistry().mapSensor(runtime, sensor.getCondition(),
				result, context);
		SensorMapperResult<Transformation> ifTrue = runtime.getMapperRegistry().mapSensor(runtime, sensor.getIfTrue(),
				result, context);
		SensorMapperResult<Transformation> ifFalse = runtime.getMapperRegistry().mapSensor(runtime, sensor.getIfFalse(),
				result, context);

		FrameConditional cond = result.add(new FrameConditional());

		result.connect(condition.getSensorPort(), cond.getInCondition(), new StateDataflow());
		result.connect(ifTrue.getSensorPort(), cond.getInTrue(), new TransformationDataflow());
		result.connect(ifFalse.getSensorPort(), cond.getInFalse(), new TransformationDataflow());

		DataflowOutPort resultPort = result.addOutPort(new TransformationDataflow(), false, cond.getOutValue());
		return new TransformationSensorMapperResult(result, resultPort);
	}

}
