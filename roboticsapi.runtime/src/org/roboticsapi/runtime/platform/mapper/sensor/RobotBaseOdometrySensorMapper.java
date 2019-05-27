/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.sensor;

import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.platform.RobotBaseOdometrySensor;
import org.roboticsapi.runtime.platform.primitives.BaseMonitor;
import org.roboticsapi.world.Transformation;

public class RobotBaseOdometrySensorMapper
		implements SensorMapper<SoftRobotRuntime, Transformation, RobotBaseOdometrySensor> {

	@Override
	public SensorMapperResult<Transformation> map(SoftRobotRuntime runtime, RobotBaseOdometrySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("Platform Odometry Sensor");
		NetFragment monitorFragment = new NetFragment("Monitor");
		BaseMonitor monitor = monitorFragment.add(new BaseMonitor(sensor.getDeviceName()));
		DataflowOutPort sensorPort = monitorFragment
				.addOutPort(new RelationDataflow(sensor.getPlatformDriver().getOdometryOrigin(),
						sensor.getPlatformDriver().getOdometryFrame()), false, monitor.getOutPos());
		fragment.add(monitorFragment);
		return new TransformationSensorMapperResult(fragment, sensorPort);
	}
}
