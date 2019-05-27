/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.DividedDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleDivide;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class DividedDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, DividedDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, DividedDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Divide<DoubleSensor>");
		SensorMapperResult<Double> dividend = runtime.getMapperRegistry().mapSensor(runtime, sensor.getDividend(),
				result, context);
		SensorMapperResult<Double> divisor = runtime.getMapperRegistry().mapSensor(runtime, sensor.getDivisor(), result,
				context);

		NetFragment val = new NetFragment("Divide");
		DoubleDivide div = val.add(new DoubleDivide());

		val.connect(dividend.getSensorPort(),
				val.addInPort(dividend.getSensorPort().getType(), true, div.getInFirst()));
		val.connect(divisor.getSensorPort(), val.addInPort(divisor.getSensorPort().getType(), true, div.getInSecond()));

		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new DoubleDataflow(), false, div.getOutValue());
		return new DoubleSensorMapperResult(result, resultPort);
	}

}
