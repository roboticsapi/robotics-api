/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.AddedIntegerSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.IntAdd;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.IntSensorMapperResult;

/**
 * {@link SensorMapper} implementation for {@link AddedIntegerSensor}.
 */
public class AddedIntegerSensorMapper implements SensorMapper<SoftRobotRuntime, Integer, AddedIntegerSensor> {

	@Override
	public SensorMapperResult<Integer> map(SoftRobotRuntime runtime, AddedIntegerSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment result = new NetFragment("Add<IntegerSensor>");

		SensorMapperResult<Integer> left = mapSensor(runtime, sensor.getAddend1(), result, context);
		SensorMapperResult<Integer> right = mapSensor(runtime, sensor.getAddend2(), result, context);
		IntAdd add = result.add(new IntAdd());

		result.connect(left.getSensorPort(), add.getInFirst(), new IntDataflow());
		result.connect(right.getSensorPort(), add.getInSecond(), new IntDataflow());

		return new IntSensorMapperResult(result, result.addOutPort(new IntDataflow(), false, add.getOutValue()));
	}

	private <T> SensorMapperResult<T> mapSensor(SoftRobotRuntime runtime, Sensor<T> sensor, NetFragment fragment,
			SensorMappingContext context) throws MappingException {
		return runtime.getMapperRegistry().mapSensor(runtime, sensor, fragment, context);
	}

}
