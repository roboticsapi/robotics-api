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
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.RotationFromAxisAngle;
import org.roboticsapi.runtime.world.result.RotationSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.RotationFromAxisAngleSensor;

public class RotationFromAxisAngleSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Rotation, RotationFromAxisAngleSensor> {

	@Override
	public SensorMapperResult<Rotation> map(AbstractMapperRuntime runtime, RotationFromAxisAngleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("RotationFromABCSensor");
		SensorMapperResult<Vector> mappedAxis = runtime.getMapperRegistry().mapSensor(runtime, sensor.getAxis(),
				fragment, context);

		SensorMapperResult<Double> mappedAngle = runtime.getMapperRegistry().mapSensor(runtime, sensor.getAngle(),
				fragment, context);

		RotationFromAxisAngle combiner = fragment.add(new RotationFromAxisAngle());
		fragment.connect(mappedAxis.getSensorPort(), combiner.getInAxis(), new VectorDataflow());
		fragment.connect(mappedAngle.getSensorPort(), combiner.getInAngle(), new DoubleDataflow());

		DataflowOutPort sensorPort = fragment.addOutPort(new RotationDataflow(), true, combiner.getOutValue());

		return new RotationSensorMapperResult(fragment, sensorPort);
	}

}
