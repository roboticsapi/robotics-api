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
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.primitives.RotationAverage;
import org.roboticsapi.runtime.world.result.RotationSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.sensor.SlidingAverageRotationSensor;

public class SlidingAverageRotationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Rotation, SlidingAverageRotationSensor> {

	@Override
	public SensorMapperResult<Rotation> map(AbstractMapperRuntime runtime, SlidingAverageRotationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("SlidingAverageRotationSensor");

		SensorMapperResult<Rotation> inner = runtime.getMapperRegistry().mapSensor(runtime, sensor.getOtherSensor(),
				fragment, context);
		RotationAverage avg = fragment.add(new RotationAverage(sensor.getDuration()));
		fragment.connect(inner.getSensorPort(), avg.getInValue(), new RotationDataflow());
		DataflowOutPort sensorPort = fragment.addOutPort(new RotationDataflow(), true, avg.getOutValue());

		return new RotationSensorMapperResult(fragment, sensorPort);
	}
}
