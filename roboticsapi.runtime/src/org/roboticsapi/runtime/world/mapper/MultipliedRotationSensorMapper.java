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
import org.roboticsapi.runtime.world.primitives.RotationTransform;
import org.roboticsapi.runtime.world.result.RotationSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.sensor.MultipliedRotationSensor;
import org.roboticsapi.world.sensor.RotationSensor;

public class MultipliedRotationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Rotation, MultipliedRotationSensor> {

	@Override
	public SensorMapperResult<Rotation> map(AbstractMapperRuntime runtime, MultipliedRotationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("TransformedRotationSensor");

		// map the original sensor
		SensorMapperResult<Rotation> mappedOtherSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), fragment, context);

		// sensor used for multiplication
		RotationSensor refTransformation = sensor.getRotationSensor();

		if (refTransformation == null) {
			throw new MappingException("For Vector reinterpretation, TransformationSensor must be set");
		}

		SensorMapperResult<Rotation> mappedTransformation = runtime.getMapperRegistry().mapSensor(runtime,
				refTransformation, fragment, context);

		// rotate the original sensor's values
		RotationTransform rotate = fragment.add(new RotationTransform());

		fragment.connect(mappedOtherSensor.getSensorPort(), rotate.getInFirst(), new RotationDataflow());
		fragment.connect(mappedTransformation.getSensorPort(), rotate.getInSecond(), new RotationDataflow());

		// the result of the RotationTransform is the end result
		DataflowOutPort sensorPort = fragment.addOutPort(new RotationDataflow(), true, rotate.getOutValue());

		return new RotationSensorMapperResult(fragment, sensorPort);
	}

}
