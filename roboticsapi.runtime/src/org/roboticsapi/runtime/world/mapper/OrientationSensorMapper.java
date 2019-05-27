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
import org.roboticsapi.runtime.world.dataflow.OrientationDataflow;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.sensor.OrientationSensor;

public class OrientationSensorMapper implements SensorMapper<AbstractMapperRuntime, Orientation, OrientationSensor> {

	@Override
	public SensorMapperResult<Orientation> map(AbstractMapperRuntime runtime, final OrientationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		SensorMapperResult<Rotation> mappedInnerSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getRotationSensor(), null, context);

		DataflowOutPort sensorPort = mappedInnerSensor.getNetFragment().reinterpret(mappedInnerSensor.getSensorPort(),
				new OrientationDataflow(sensor.getReference()));

		try {
			return new OrientationSensorMapperResult(sensor, mappedInnerSensor, sensorPort);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
	}

	private class OrientationSensorMapperResult extends BaseSensorMapperResult<Orientation> {

		private final SensorMapperResult<Rotation> innerSensor;
		private final OrientationSensor sensor;

		public OrientationSensorMapperResult(final OrientationSensor sensor, SensorMapperResult<Rotation> innerSensor,
				DataflowOutPort sensorPort) throws RoboticsException {
			super(innerSensor.getNetFragment(), sensorPort);
			this.sensor = sensor;
			this.innerSensor = innerSensor;
		}

		@Override
		public void addListener(Command command, final SensorListener<Orientation> listener) throws MappingException {
			innerSensor.addListener(command, new SensorListener<Rotation>() {
				@Override
				public void onValueChanged(Rotation newValue) {
					listener.onValueChanged(new Orientation(sensor.getReference(), newValue));
				}
			});
		}

		@Override
		public void addUpdateListener(Command command, final SensorUpdateListener<Orientation> listener)
				throws MappingException {
			innerSensor.addUpdateListener(command, new SensorUpdateListener<Rotation>() {
				@Override
				public void onValueChanged(Rotation newValue) {
					listener.onValueChanged(new Orientation(sensor.getReference(), newValue));
				}

				@Override
				public void updatePerformed() {
					listener.updatePerformed();
				}
			});
		}

		@Override
		public void assign(Command command, PersistContext<Orientation> target) throws MappingException {
			// FIXME add
		}
	}
}
