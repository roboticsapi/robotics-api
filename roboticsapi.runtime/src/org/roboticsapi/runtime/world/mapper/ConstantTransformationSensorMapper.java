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
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.ConstantTransformationSensor;

public class ConstantTransformationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, ConstantTransformationSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, ConstantTransformationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("ConstantTransformationSensor");
		NetFragment ret = new NetFragment("Transformation");
		Transformation trans = sensor.getTransformation();

		final FrameFromPosRot fc = new FrameFromPosRot(trans.getTranslation().getX(), trans.getTranslation().getY(),
				trans.getTranslation().getZ(), trans.getRotation().getA(), trans.getRotation().getB(),
				trans.getRotation().getC());
		ret.add(fc);
		DataflowOutPort resultPort = ret.addOutPort(new TransformationDataflow(), true, fc.getOutValue());

		result.add(ret);
		return new TransformationSensorMapperResult(result, resultPort);
	}
}
