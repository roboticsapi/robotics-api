/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.SlidingAverageDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleAverage;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class SlidingAverageDoubleSensorMapper
		implements SensorMapper<SoftRobotRuntime, Double, SlidingAverageDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, SlidingAverageDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("SlidingAverageDoubleSensor");
		SensorMapperResult<Double> mappedInnerSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), fragment, context);

		DoubleAverage average = fragment.add(new DoubleAverage(sensor.getDuration()));

		fragment.connect(mappedInnerSensor.getSensorPort(), average.getInValue(), new DoubleDataflow());

		return new DoubleSensorMapperResult(fragment,
				fragment.addOutPort(new DoubleDataflow(), true, average.getOutValue()));
	}
}
