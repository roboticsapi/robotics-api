/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.core.primitives.DoubleArrayGet;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DoubleArrayDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.FromArrayTransformationSensor;

public class FromArrayTransformationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, FromArrayTransformationSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, FromArrayTransformationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("DoubleFromDoubleArraySensor");

		SensorMapperResult<Transformation[]> mappedOther = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getTransformationArray(), fragment, context);
		DoubleArrayGet get = fragment
				.add(new DoubleArrayGet(sensor.getTransformationArray().getSize(), sensor.getIndex()));
		fragment.connect(mappedOther.getSensorPort(), get.getInArray(),
				new DoubleArrayDataflow(sensor.getTransformationArray().getSize()));
		return new TransformationSensorMapperResult(fragment,
				fragment.addOutPort(new TransformationDataflow(), false, get.getOutValue()));
	}
}
