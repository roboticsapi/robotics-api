/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.sensor;

import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.platform.CartesianCommandedPositionSensor;
import org.roboticsapi.runtime.platform.primitives.CartesianMonitor;
import org.roboticsapi.world.Transformation;

public class CartesianCommandedPositionSensorMapper
		implements SensorMapper<SoftRobotRuntime, Transformation, CartesianCommandedPositionSensor> {

	@Override
	public SensorMapperResult<Transformation> map(SoftRobotRuntime runtime, CartesianCommandedPositionSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("CartesianCommandedPositionSensor");
		CartesianMonitor monitor = fragment.add(new CartesianMonitor(sensor.getDeviceName()));
		return new TransformationSensorMapperResult(fragment,
				fragment.addOutPort(new TransformationDataflow(), false, monitor.getOutCommandedPosition()));
	}
}
