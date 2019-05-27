/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.MultipliedDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class MultipliedDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, MultipliedDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, MultipliedDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment result = new NetFragment("Multiply<DoubleSensor>");

		SensorMapperResult<Double> left = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor1(), result,
				context);
		SensorMapperResult<Double> right;
		if (sensor.getSensor1() == sensor.getSensor2()) {

			right = left;
		} else {
			right = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor2(), result, context);
			result.add(right.getNetFragment());
		}

		result.add(left.getNetFragment());

		NetFragment val = new NetFragment("Multiply");
		DoubleMultiply mult = val.add(new DoubleMultiply());

		val.connect(left.getSensorPort(), val.addInPort(left.getSensorPort().getType(), true, mult.getInFirst()));
		val.connect(right.getSensorPort(), val.addInPort(right.getSensorPort().getType(), true, mult.getInSecond()));

		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new DoubleDataflow(), false, mult.getOutValue());
		return new DoubleSensorMapperResult(result, resultPort);
	}

}
