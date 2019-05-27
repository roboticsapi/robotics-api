/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.world.WorldLinkBuilder;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.driver.mapper.SoftRobotMultiJointDeviceDriverJointPositionMapper;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotRobotArmDriverJointPositionMapper<DD extends SoftRobotRobotArmDriver>
		extends SoftRobotMultiJointDeviceDriverJointPositionMapper<DD> implements SoftRobotRobotArmMapper {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, final DD driver,
			JointPositionActionResult actionResult, final DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		ActuatorDriverMapperResult mapResult = super.map(runtime, driver, actionResult, parameters, ports);
		NetFragment fragment = mapResult.getNetFragment();

		// add inverse kinematics as a LinkBuilder
		fragment.addLinkBuilder(new WorldLinkBuilder(runtime));
		fragment.addLinkBuilder(new SoftRobotRobotArmLinkBuilder(parameters, driver, this));

		return mapResult;
	}

	@Override
	public InvKinematicsFragment createInvKinematicsFragment(final SoftRobotRobotArmDriver robot,
			final DeviceParameterBag parameters) throws MappingException {
		return new RobotArmInvKinematicsFragment(robot, parameters);
	}

}
