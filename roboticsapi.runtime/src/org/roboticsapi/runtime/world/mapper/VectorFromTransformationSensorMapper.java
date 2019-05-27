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
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.FrameToPosRot;
import org.roboticsapi.runtime.world.result.VectorSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.VectorFromTransformationSensor;

public class VectorFromTransformationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Vector, VectorFromTransformationSensor> {

	@Override
	public SensorMapperResult<Vector> map(AbstractMapperRuntime runtime, VectorFromTransformationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("TranslationFromTransformationSensor");

		SensorMapperResult<Transformation> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getTransformationSensor(), fragment, context);

		NetFragment splitFrag = fragment.add(new NetFragment("Splitting"));

		FrameToPosRot split = splitFrag.add(new FrameToPosRot());

		splitFrag.connect(mappedSensor.getSensorPort(),
				splitFrag.addInPort(new TransformationDataflow(), true, split.getInValue()));

		DataflowOutPort sensorPort = fragment.addOutPort(new VectorDataflow(), true, split.getOutPosition());

		return new VectorSensorMapperResult(fragment, sensorPort);
	}
}
