/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotMultiJointDeviceDriver;
import org.roboticsapi.runtime.multijoint.mapper.AbstractMultiJointDeviceDriverMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointGoalActionResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotMultiJointDeviceDriverJointGoalMapper<DD extends SoftRobotMultiJointDeviceDriver>
		extends AbstractSoftRobotMultiJointDeviceDriverMapper<DD, JointGoalActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, DD driver, JointGoalActionResult actionResult,
			DeviceParameterBag parameters, DeviceMappingPorts ports) throws MappingException, RPIException {

		NetFragment fragment = new NetFragment("SoftRobotMultiJointDevice (Goal)");

		JointDeviceParameters jointParameters = parameters.get(JointDeviceParameters.class);

		DataflowThroughOutPort outPos = fragment.addThroughPort(true, getJointsDataflowType(driver));

		fragment.connect(actionResult.getOutPort(), outPos.getInPort());
		ActuatorDriverMapperResult[] jointResult = new ActuatorDriverMapperResult[driver.getJointCount()];

		for (int i = 0; i < driver.getJointCount(); i++) {
			DeviceParameterBag dParameters = parameters;
			if (jointParameters != null) {
				JointParameters jointParameter = jointParameters.getJointParameters(i);
				if (jointParameter != null) {
					dParameters = parameters.withParameters(jointParameter);
				}
			}

			jointResult[i] = runtime.getMapperRegistry().mapActuatorDriver(runtime, driver.getJoint(i).getDriver(),
					new JointGoalActionResult(outPos), dParameters, ports.cancel, ports.override);
			fragment.add(jointResult[i].getNetFragment());
		}

		fragment.addLinkBuilder(new SoftRobotMultijointLinkBuilder(runtime, driver, this));

		return new AbstractMultiJointDeviceDriverMapperResult<MultiJointDeviceDriver>(driver, fragment, parameters,
				runtime, jointResult) {
		};
	}

}
