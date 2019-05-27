/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.sensor;

import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.platform.CartesianMeasuredVelocitySensor;
import org.roboticsapi.runtime.platform.primitives.CartesianMonitor;
import org.roboticsapi.world.Twist;

public class CartesianMeasuredVelocitySensorMapper
		implements SensorMapper<SoftRobotRuntime, Twist, CartesianMeasuredVelocitySensor> {

	@Override
	public SensorMapperResult<Twist> map(SoftRobotRuntime runtime, CartesianMeasuredVelocitySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("CartesianCommandedVelocitySensor");
		CartesianMonitor monitor = fragment.add(new CartesianMonitor(sensor.getDeviceName()));
		DataflowOutPort sensorPort = fragment
				.addOutPort(
						new VelocityDataflow(sensor.getMovingFrame(), sensor.getReferenceFrame(),
								sensor.getPivotPoint(), sensor.getOrientation()),
						false, monitor.getOutMeasuredVelocity());
		return new VelocitySensorMapperResult(fragment, sensorPort);
	}
}
