/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.sensor;

import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.platform.PlatformWheelPositionDoubleSensor;
import org.roboticsapi.runtime.platform.primitives.WheelMonitor;

public class PlatformWheelPositionDoubleSensorMapper
		implements SensorMapper<SoftRobotRuntime, Double, PlatformWheelPositionDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(final SoftRobotRuntime runtime,
			final PlatformWheelPositionDoubleSensor sensor, SensorMappingPorts ports, SensorMappingContext context)
			throws MappingException {

		NetFragment fragment = new NetFragment("Platform Wheel Position Sensor");
		WheelMonitor monitor = fragment
				.add(new WheelMonitor(sensor.getDriver().getDeviceName(), sensor.getWheelNumber()));

		DataflowOutPort sensorPort = fragment.addOutPort(new DoubleDataflow(), false, monitor.getOutPos());

		return new DoubleSensorMapperResult(fragment, sensorPort);

	}
}
