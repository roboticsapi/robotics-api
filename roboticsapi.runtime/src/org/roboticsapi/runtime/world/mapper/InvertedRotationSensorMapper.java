/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.primitives.RotationInvert;
import org.roboticsapi.runtime.world.result.RotationSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.sensor.InvertedRotationSensor;

public class InvertedRotationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Rotation, InvertedRotationSensor> {

	@Override
	public SensorMapperResult<Rotation> map(AbstractMapperRuntime runtime, InvertedRotationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment result = new NetFragment("InvertedRotationSensor");

		SensorMapperResult<Rotation> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), result, context);

		RotationInvert inverter = result.add(new RotationInvert());

		result.connect(mappedSensor.getSensorPort(), inverter.getInValue(), new RotationDataflow());

		return new RotationSensorMapperResult(result,
				result.addOutPort(new RotationDataflow(), true, inverter.getOutValue()));
	}

}
