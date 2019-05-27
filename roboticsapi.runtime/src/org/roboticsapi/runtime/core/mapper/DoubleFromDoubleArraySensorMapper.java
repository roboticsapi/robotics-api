/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.DoubleFromDoubleArraySensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleArrayGet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DoubleArrayDataflow;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class DoubleFromDoubleArraySensorMapper
		implements SensorMapper<SoftRobotRuntime, Double, DoubleFromDoubleArraySensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, DoubleFromDoubleArraySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("DoubleFromDoubleArraySensor");

		SensorMapperResult<Double[]> mappedOther = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getDoubleArray(), fragment, context);
		int size = sensor.getDoubleArray().getSize();

		DoubleArrayGet get = fragment.add(new DoubleArrayGet(size, sensor.getIndex()));
		fragment.connect(mappedOther.getSensorPort(), get.getInArray(), new DoubleArrayDataflow(size));
		return new DoubleSensorMapperResult(fragment,
				fragment.addOutPort(new DoubleDataflow(), false, get.getOutValue()));
	}
}
