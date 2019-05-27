/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.fragment.AddVelocitiesFragment;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.AddedVelocitySensor;

public class AddedVelocitySensorMapper implements SensorMapper<AbstractMapperRuntime, Twist, AddedVelocitySensor> {

	@Override
	public SensorMapperResult<Twist> map(AbstractMapperRuntime runtime, AddedVelocitySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		SensorMapperResult<Twist> firstSensor = runtime.getMapperRegistry().mapSensor(runtime, sensor.getFirstSensor(),
				null, context);

		SensorMapperResult<Twist> secondSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getSecondSensor(), null, context);

		AddVelocitiesFragment result = new AddVelocitiesFragment("AddedVelocitySensor", firstSensor.getSensorPort(),
				secondSensor.getSensorPort());

		result.add(firstSensor.getNetFragment());
		result.add(secondSensor.getNetFragment());

		return new VelocitySensorMapperResult(result, result.getAddedVelocitiesPort());

	}
}
