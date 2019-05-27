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
import org.roboticsapi.runtime.world.fragment.InvertVelocityFragment;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.InvertedVelocitySensor;

public class InvertedVelocitySensorMapper
		implements SensorMapper<AbstractMapperRuntime, Twist, InvertedVelocitySensor> {

	@Override
	public SensorMapperResult<Twist> map(AbstractMapperRuntime runtime, InvertedVelocitySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		SensorMapperResult<Twist> other = runtime.getMapperRegistry().mapSensor(runtime, sensor.getOtherSensor(), null,
				context);

		InvertVelocityFragment result = new InvertVelocityFragment("InvertedVelocitySensor", other.getSensorPort());
		result.add(other.getNetFragment());

		return new VelocitySensorMapperResult(result, result.getInvertedVelocityPort());

	}

}
