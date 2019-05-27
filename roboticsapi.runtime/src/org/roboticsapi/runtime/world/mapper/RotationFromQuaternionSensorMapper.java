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
import org.roboticsapi.runtime.world.primitives.RotationFromQuaternion;
import org.roboticsapi.runtime.world.result.RotationSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.sensor.RotationFromQuaternionSensor;

public class RotationFromQuaternionSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Rotation, RotationFromQuaternionSensor> {

	@Override
	public SensorMapperResult<Rotation> map(AbstractMapperRuntime runtime, RotationFromQuaternionSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("RotationFromQuaternionSensor");
		SensorMapperResult<Double> mappedX = runtime.getMapperRegistry().mapSensor(runtime, sensor.getXComponent(),
				fragment, context);
		SensorMapperResult<Double> mappedY = runtime.getMapperRegistry().mapSensor(runtime, sensor.getYComponent(),
				fragment, context);
		SensorMapperResult<Double> mappedZ = runtime.getMapperRegistry().mapSensor(runtime, sensor.getZComponent(),
				fragment, context);
		SensorMapperResult<Double> mappedW = runtime.getMapperRegistry().mapSensor(runtime, sensor.getWComponent(),
				fragment, context);

		RotationFromQuaternion combiner = fragment.add(new RotationFromQuaternion());
		fragment.connect(mappedX.getSensorPort(), combiner.getInX(), new DoubleDataflow());
		fragment.connect(mappedY.getSensorPort(), combiner.getInY(), new DoubleDataflow());
		fragment.connect(mappedZ.getSensorPort(), combiner.getInZ(), new DoubleDataflow());
		fragment.connect(mappedW.getSensorPort(), combiner.getInW(), new DoubleDataflow());

		DataflowOutPort sensorPort = fragment.addOutPort(new RotationDataflow(), true, combiner.getOutValue());

		return new RotationSensorMapperResult(fragment, sensorPort);
	}

}
