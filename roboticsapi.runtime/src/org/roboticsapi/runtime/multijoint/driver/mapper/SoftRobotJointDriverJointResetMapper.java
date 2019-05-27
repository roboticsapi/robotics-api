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
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotJointDriver;
import org.roboticsapi.runtime.multijoint.mapper.JointDriverMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointResetActionResult;
import org.roboticsapi.runtime.multijoint.mapper.JointDriverMapperResult.JointPorts;
import org.roboticsapi.runtime.multijoint.primitives.JointPosition;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotJointDriverJointResetMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotJointDriver, JointResetActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, final SoftRobotJointDriver jointDriver,
			JointResetActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		NetFragment fragment = new NetFragment(jointDriver.getName());

		final JointPosition jointPosition = fragment
				.add(new JointPosition(jointDriver.getDeviceName(), jointDriver.getJointNumber()));

		fragment.connect(actionResult.getOutPort(), jointPosition.getInPosition(), new JointDataflow(jointDriver));

		DataflowOutPort concurrentAccessPort = fragment.addOutPort(new StateDataflow(), true,
				jointPosition.getOutErrorConcurrentAccess());
		DataflowOutPort drivesNotEnabledPort = fragment.addOutPort(new StateDataflow(), true,
				jointPosition.getOutErrorJointFailed());
		DataflowOutPort illegalJointValuePort = fragment.addOutPort(new StateDataflow(), true,
				jointPosition.getOutErrorIllegalPosition());

		DataflowThroughOutPort joint = fragment.addThroughPort(false, new JointDataflow(jointDriver));
		JointPorts jointPorts = new JointDriverMapperResult.JointPorts(joint.getInPort(), concurrentAccessPort,
				drivesNotEnabledPort, illegalJointValuePort, null);

		return new JointDriverMapperResult<SoftRobotJointDriver>(jointDriver, fragment, jointPorts);
	}

}
