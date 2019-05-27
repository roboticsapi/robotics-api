/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.DoubleEqualsSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleEquals;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;

public class DoubleEqualsSensorMapper implements SensorMapper<SoftRobotRuntime, Boolean, DoubleEqualsSensor> {

	@Override
	public SensorMapperResult<Boolean> map(SoftRobotRuntime runtime, DoubleEqualsSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Double Equals");
		SensorMapperResult<Double> left = runtime.getMapperRegistry().mapSensor(runtime, sensor.getLeft(), result,
				context);
		SensorMapperResult<Double> right = runtime.getMapperRegistry().mapSensor(runtime, sensor.getRight(), result,
				context);

		NetFragment val = new NetFragment("Comparison");
		DoubleEquals greater = val.add(new DoubleEquals(sensor.getEpsilon()));

		val.connect(left.getSensorPort(), val.addInPort(left.getSensorPort().getType(), true, greater.getInFirst()));
		val.connect(right.getSensorPort(), val.addInPort(right.getSensorPort().getType(), true, greater.getInSecond()));

		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new StateDataflow(), false, greater.getOutValue());
		return new BooleanSensorMapperResult(result, resultPort);
	}
}
