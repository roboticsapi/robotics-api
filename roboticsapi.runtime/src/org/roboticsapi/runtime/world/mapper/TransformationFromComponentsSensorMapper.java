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
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.primitives.RotationFromABC;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.TransformationFromComponentsSensor;

public class TransformationFromComponentsSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation, TransformationFromComponentsSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime,
			TransformationFromComponentsSensor sensor, SensorMappingPorts ports, SensorMappingContext context)
			throws MappingException {

		NetFragment result = new NetFragment("TransformationFromComponentsSensor");
		NetFragment ret = new NetFragment("Transformation");

		final RotationFromABC rot = ret.addPrimitive(new RotationFromABC());
		final VectorFromXYZ pos = ret.addPrimitive(new VectorFromXYZ());
		final FrameFromPosRot fc = ret.addPrimitive(new FrameFromPosRot());

		SensorMapperResult<Double> a = runtime.getMapperRegistry().mapSensor(runtime, sensor.getA(), result, context);
		ret.connect(a.getSensorPort(), ret.addInPort(new DoubleDataflow(), true, rot.getInA()));

		SensorMapperResult<Double> b = runtime.getMapperRegistry().mapSensor(runtime, sensor.getB(), result, context);
		ret.connect(b.getSensorPort(), ret.addInPort(new DoubleDataflow(), true, rot.getInB()));

		SensorMapperResult<Double> c = runtime.getMapperRegistry().mapSensor(runtime, sensor.getC(), result, context);
		ret.connect(c.getSensorPort(), ret.addInPort(new DoubleDataflow(), true, rot.getInC()));

		SensorMapperResult<Double> x = runtime.getMapperRegistry().mapSensor(runtime, sensor.getX(), result, context);
		ret.connect(x.getSensorPort(), ret.addInPort(new DoubleDataflow(), true, pos.getInX()));

		SensorMapperResult<Double> y = runtime.getMapperRegistry().mapSensor(runtime, sensor.getY(), result, context);
		ret.connect(y.getSensorPort(), ret.addInPort(new DoubleDataflow(), true, pos.getInY()));

		SensorMapperResult<Double> z = runtime.getMapperRegistry().mapSensor(runtime, sensor.getZ(), result, context);
		ret.connect(z.getSensorPort(), ret.addInPort(new DoubleDataflow(), true, pos.getInZ()));
		try {

			fc.getInRot().connectTo(rot.getOutValue());
			fc.getInPos().connectTo(pos.getOutValue());
		} catch (RPIException ex) {
			throw new MappingException(ex);
		}
		DataflowOutPort resultPort = ret.addOutPort(new TransformationDataflow(), true, fc.getOutValue());

		result.add(ret);
		return new TransformationSensorMapperResult(result, resultPort);
	}
}
