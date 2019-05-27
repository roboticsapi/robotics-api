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
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.FrameToPosRot;
import org.roboticsapi.runtime.world.primitives.VectorAdd;
import org.roboticsapi.runtime.world.primitives.VectorRotate;
import org.roboticsapi.runtime.world.result.VectorSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.TransformedVectorSensor;

public class ReferenceAdaptedPointSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Vector, TransformedVectorSensor> {

	@Override
	public SensorMapperResult<Vector> map(AbstractMapperRuntime runtime, TransformedVectorSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("TransformedVectorSensor");

		// map the original sensor
		SensorMapperResult<Vector> mappedOtherSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), fragment, context);

		// create a TransformationSensor representing the reference frame
		// transformation...
		TransformationSensor refTransformation = sensor.getTransformationSensor();

		if (refTransformation == null) {
			throw new MappingException("For Vector reinterpretation, TransformationSensor must be set");
		}

		// ... and map it
		SensorMapperResult<Transformation> mappedTransformation = runtime.getMapperRegistry().mapSensor(runtime,
				refTransformation, fragment, context);

		// separate fragment for the vector transformation process
		NetFragment transformFragment = fragment.add(new NetFragment("Vector transformation"));

		// split the calculated transformation into position and rotation parts
		FrameToPosRot splitter = transformFragment.add(new FrameToPosRot());
		transformFragment.connect(mappedTransformation.getSensorPort(),
				transformFragment.addInPort(new TransformationDataflow(), true, splitter.getInValue()));

		// first, rotate the original sensor's vector
		VectorRotate rotate = transformFragment.add(new VectorRotate());
		transformFragment.connect(mappedOtherSensor.getSensorPort(),
				transformFragment.addInPort(new VectorDataflow(), true, rotate.getInValue()));
		try {
			rotate.getInRot().connectTo(splitter.getOutRotation());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		// second, add the reference frame translation to the rotated vector
		VectorAdd add = transformFragment.add(new VectorAdd());
		try {
			add.getInFirst().connectTo(rotate.getOutValue());
			add.getInSecond().connectTo(splitter.getOutPosition());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		// the result of the addition is the end result
		DataflowOutPort sensorPort = fragment.addOutPort(new VectorDataflow(), true, add.getOutValue());

		return new VectorSensorMapperResult(fragment, sensorPort);
	}
}
