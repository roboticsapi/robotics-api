/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.MultipliedIntegerSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.IntMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.IntSensorMapperResult;

/**
 * {@link SensorMapper} implementation for {@link MultipliedIntegerSensor}.
 */
public class MultipliedIntegerSensorMapper implements SensorMapper<SoftRobotRuntime, Integer, MultipliedIntegerSensor> {

	@Override
	public SensorMapperResult<Integer> map(SoftRobotRuntime runtime, MultipliedIntegerSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment result = new NetFragment("Multiply<IntegerSensor>");

		SensorMapperResult<Integer> left = mapSensor(runtime, sensor.getMultiplicand(), result, context);
		SensorMapperResult<Integer> right = mapSensor(runtime, sensor.getMultiplier(), result, context);
		IntMultiply multiply = result.add(new IntMultiply());

		result.connect(left.getSensorPort(), multiply.getInFirst(), new IntDataflow());
		result.connect(right.getSensorPort(), multiply.getInSecond(), new IntDataflow());

		return new IntSensorMapperResult(result, result.addOutPort(new IntDataflow(), false, multiply.getOutValue()));
	}

	private <T> SensorMapperResult<T> mapSensor(SoftRobotRuntime runtime, Sensor<T> sensor, NetFragment fragment,
			SensorMappingContext context) throws MappingException {
		return runtime.getMapperRegistry().mapSensor(runtime, sensor, fragment, context);
	}

}
