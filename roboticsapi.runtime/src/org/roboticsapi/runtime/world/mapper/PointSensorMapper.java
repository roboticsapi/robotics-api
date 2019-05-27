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
import org.roboticsapi.runtime.world.dataflow.PointDataflow;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.PointSensor;

public class PointSensorMapper implements SensorMapper<AbstractMapperRuntime, Point, PointSensor> {

	@Override
	public SensorMapperResult<Point> map(AbstractMapperRuntime runtime, final PointSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		SensorMapperResult<Vector> mappedInnerSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getVectorSensor(), null, context);

		DataflowOutPort sensorPort = mappedInnerSensor.getNetFragment().reinterpret(mappedInnerSensor.getSensorPort(),
				new PointDataflow(sensor.getReferenceFrame()));

		try {
			return new PointSensorMapperResult(sensor, mappedInnerSensor, sensorPort);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
	}

	private class PointSensorMapperResult extends BaseSensorMapperResult<Point> {

		private final SensorMapperResult<Vector> innerSensor;
		private final PointSensor sensor;

		public PointSensorMapperResult(final PointSensor sensor, SensorMapperResult<Vector> innerSensor,
				DataflowOutPort sensorPort) throws RoboticsException {
			super(innerSensor.getNetFragment(), sensorPort);
			this.sensor = sensor;
			this.innerSensor = innerSensor;
		}

		@Override
		public void addListener(Command command, final SensorListener<Point> listener) throws MappingException {
			innerSensor.addListener(command, new SensorListener<Vector>() {
				@Override
				public void onValueChanged(Vector newValue) {
					listener.onValueChanged(new Point(sensor.getReferenceFrame(), newValue));
				}
			});
		}

		@Override
		public void addUpdateListener(Command command, final SensorUpdateListener<Point> listener)
				throws MappingException {
			innerSensor.addUpdateListener(command, new SensorUpdateListener<Vector>() {
				@Override
				public void onValueChanged(Vector newValue) {
					listener.onValueChanged(new Point(sensor.getReferenceFrame(), newValue));
				}

				@Override
				public void updatePerformed() {
					listener.updatePerformed();
				}
			});
		}

		@Override
		public void assign(Command command, PersistContext<Point> target) throws MappingException {
			// FIXME add
		}
	}
}
