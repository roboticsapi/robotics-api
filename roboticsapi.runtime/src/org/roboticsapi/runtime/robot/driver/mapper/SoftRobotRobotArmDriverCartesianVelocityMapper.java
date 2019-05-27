/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianVelocityActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotRobotArmDriverCartesianVelocityMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotRobotArmDriver, CartesianVelocityActionResult> {

	@Override
	public ActuatorDriverMapperResult map(final SoftRobotRuntime runtime, final SoftRobotRobotArmDriver driver,
			CartesianVelocityActionResult actionResult, final DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		return runtime.getMapperRegistry().mapActuatorDriver(runtime, driver,
				new JointPositionActionResult(actionResult.getOutPort()), parameters, ports.cancel, ports.override);
	}

}
