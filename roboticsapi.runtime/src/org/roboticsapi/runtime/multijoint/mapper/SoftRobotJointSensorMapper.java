/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotJointDriver;
import org.roboticsapi.runtime.multijoint.primitives.JointMonitor;
import org.roboticsapi.runtime.rpi.OutPort;

public class SoftRobotJointSensorMapper
		implements SensorMapper<SoftRobotRuntime, Double, SoftRobotJointDriver.SoftRobotJointSensor> {

	@Override
	public SensorMapperResult<Double> map(final SoftRobotRuntime runtime,
			final SoftRobotJointDriver.SoftRobotJointSensor sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {
		SoftRobotJointDriver jointDriver = sensor.getDriver();

		final NetFragment ret = new NetFragment("SoftRobotJointSensor");
		final JointMonitor lfm = new JointMonitor(jointDriver.getDeviceName(), jointDriver.getJointNumber());

		ret.add(lfm);

		for (OutPort out : lfm.getOutPorts()) {
			if (out.getName().equals(sensor.getPortName())) {
				DataflowOutPort port = ret.addOutPort(new JointDataflow(jointDriver), true, out);
				return new DoubleSensorMapperResult(ret, port);
			}
		}

		return null;
	}

}
