/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotJointDriver;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotJointDriverJointPositionMapper
		extends AbstractSoftRobotJointDriverMapper<JointPositionActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, SoftRobotJointDriver actuatorRuntimeAdapter,
			JointPositionActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		return super.map(runtime, actuatorRuntimeAdapter, actionResult, parameters, ports);
	}

}
