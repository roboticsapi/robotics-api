/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.DoubleConditionalSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class DoubleConditionalSensorMapper implements SensorMapper<SoftRobotRuntime, Double, DoubleConditionalSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, DoubleConditionalSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("DoubleConditional");
		SensorMapperResult<Boolean> condition = runtime.getMapperRegistry().mapSensor(runtime, sensor.getCondition(),
				result, context);
		SensorMapperResult<Double> ifTrue = runtime.getMapperRegistry().mapSensor(runtime, sensor.getIfTrue(), result,
				context);
		SensorMapperResult<Double> ifFalse = runtime.getMapperRegistry().mapSensor(runtime, sensor.getIfFalse(), result,
				context);

		DoubleConditional cond = result.add(new DoubleConditional());

		result.connect(condition.getSensorPort(), cond.getInCondition(), new StateDataflow());
		result.connect(ifTrue.getSensorPort(), cond.getInTrue(), new DoubleDataflow());
		result.connect(ifFalse.getSensorPort(), cond.getInFalse(), new DoubleDataflow());

		DataflowOutPort resultPort = result.addOutPort(new DoubleDataflow(), false, cond.getOutValue());
		return new DoubleSensorMapperResult(result, resultPort);
	}

}
