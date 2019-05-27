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
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseSensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.DirectionDataflow;
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.DirectionSensor;

public class DirectionSensorMapper implements SensorMapper<AbstractMapperRuntime, Direction, DirectionSensor> {

	@Override
	public SensorMapperResult<Direction> map(AbstractMapperRuntime runtime, final DirectionSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		SensorMapperResult<Vector> mappedInnerSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getVectorSensor(), null, context);

		DataflowOutPort sensorPort = mappedInnerSensor.getNetFragment().reinterpret(mappedInnerSensor.getSensorPort(),
				new DirectionDataflow(sensor.getOrientation()));

		try {
			return new DirectionSensorMapperResult(sensor, mappedInnerSensor, sensorPort);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
	}

	private class DirectionSensorMapperResult extends BaseSensorMapperResult<Direction> {

		private final SensorMapperResult<Vector> innerSensor;
		private final DirectionSensor sensor;

		public DirectionSensorMapperResult(final DirectionSensor sensor, SensorMapperResult<Vector> innerSensor,
				DataflowOutPort sensorPort) throws RoboticsException {
			super(innerSensor.getNetFragment(), sensorPort);
			this.sensor = sensor;
			this.innerSensor = innerSensor;
		}

		@Override
		public void addListener(Command command, final SensorListener<Direction> listener) throws MappingException {
			innerSensor.addListener(command, new SensorListener<Vector>() {
				@Override
				public void onValueChanged(Vector newValue) {
					listener.onValueChanged(new Direction(sensor.getOrientation(), newValue));
				}
			});
		}

		@Override
		public void addUpdateListener(Command command, final SensorUpdateListener<Direction> listener)
				throws MappingException {
			innerSensor.addUpdateListener(command, new SensorUpdateListener<Vector>() {
				@Override
				public void onValueChanged(Vector newValue) {
					listener.onValueChanged(new Direction(sensor.getOrientation(), newValue));
				}

				@Override
				public void updatePerformed() {
					listener.updatePerformed();
				}
			});
		}

		@Override
		public void assign(Command command, PersistContext<Direction> target) throws MappingException {
			// FIXME add
		}
	}
}
