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
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameToPosRot;
import org.roboticsapi.runtime.world.result.RotationSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.RotationFromTransformationSensor;

public class RotationFromTransformationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Rotation, RotationFromTransformationSensor> {

	@Override
	public SensorMapperResult<Rotation> map(AbstractMapperRuntime runtime, RotationFromTransformationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("RotationFromTransformationSensor");

		SensorMapperResult<Transformation> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getTransformationSensor(), fragment, context);

		NetFragment splitFrag = fragment.add(new NetFragment("Componentization"));

		FrameToPosRot split = splitFrag.add(new FrameToPosRot());

		splitFrag.connect(mappedSensor.getSensorPort(),
				splitFrag.addInPort(new TransformationDataflow(), true, split.getInValue()));

		DataflowOutPort sensorPort = fragment.addOutPort(new RotationDataflow(), true, split.getOutRotation());

		return new RotationSensorMapperResult(fragment, sensorPort);
	}

}
