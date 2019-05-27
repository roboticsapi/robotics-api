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
import org.roboticsapi.runtime.world.dataflow.TransformationDataflowSensor;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;

public class TransformationDataflowSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, TransformationDataflowSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, TransformationDataflowSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		return new TransformationSensorMapperResult(new NetFragment("TransformationDataflowSensor"),
				sensor.getTransformationDataflowPort());
	}

}
