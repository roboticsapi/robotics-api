/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseSensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.RelationSensor;

public class RelationSensorMapper implements SensorMapper<AbstractMapperRuntime, Transformation, RelationSensor> {

	@Override
	public SensorMapperResult<Transformation> map(AbstractMapperRuntime runtime, final RelationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		final SensorMapperResult<Transformation> mappedInnerSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getTransformationSensor(), null, context);

		DataflowOutPort sensorPort = mappedInnerSensor.getNetFragment().reinterpret(mappedInnerSensor.getSensorPort(),
				new RelationDataflow(sensor.getFrom(), sensor.getTo()));

		return new BaseSensorMapperResult<Transformation>(mappedInnerSensor.getNetFragment(), sensorPort,
				mappedInnerSensor.getSensorTimePort()) {

			@Override
			public void addListener(Command command, SensorListener<Transformation> listener) throws MappingException {
				mappedInnerSensor.addListener(command, listener);

			}

			@Override
			public void addUpdateListener(Command command, SensorUpdateListener<Transformation> listener)
					throws MappingException {
				addListener(command, listener);

			}

			@Override
			public void assign(Command command, PersistContext<Transformation> target) throws MappingException {
				// FIXME add
			}
		};
	}
}
