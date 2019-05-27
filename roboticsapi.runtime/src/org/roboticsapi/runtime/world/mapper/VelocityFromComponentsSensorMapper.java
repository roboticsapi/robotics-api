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
import org.roboticsapi.runtime.world.dataflow.DirectionDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.VelocityFromComponentsSensor;

public class VelocityFromComponentsSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Twist, VelocityFromComponentsSensor> {

	@Override
	public SensorMapperResult<Twist> map(AbstractMapperRuntime runtime, VelocityFromComponentsSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("VelocityFromComponentsSensor");

		SensorMapperResult<Direction> mappedTSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getTranslationVelocitySensor(), fragment, context);

		SensorMapperResult<Direction> mappedRSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getRotationVelocitySensor(), fragment, context);

		NetFragment combine = fragment.add(new NetFragment("Direction combination"));

		TwistFromVelocities combiner = combine.add(new TwistFromVelocities());

		combine.connect(mappedTSensor.getSensorPort(),
				combine.addInPort(new DirectionDataflow(sensor.getOrientation()), true, combiner.getInTransVel()));
		combine.connect(mappedRSensor.getSensorPort(),
				combine.addInPort(new DirectionDataflow(sensor.getOrientation()), true, combiner.getInRotVel()));

		DataflowOutPort sensorPort = combine.addOutPort(new VelocityDataflow(sensor.getMovingFrame(),
				sensor.getReferenceFrame(), sensor.getPivotPoint(), sensor.getOrientation()), true,
				combiner.getOutValue());

		return new VelocitySensorMapperResult(fragment, sensorPort);
	}

}
