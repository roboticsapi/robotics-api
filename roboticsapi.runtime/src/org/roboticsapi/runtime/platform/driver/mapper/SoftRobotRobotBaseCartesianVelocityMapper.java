/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.world.WorldLinkBuilder;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianVelocityActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.ErrorNumberSwitchFragment;
import org.roboticsapi.runtime.mapping.parts.TrueFragment;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.platform.driver.SoftRobotRobotBaseDriver;
import org.roboticsapi.runtime.platform.primitives.BaseVelocity;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.world.Frame;

public class SoftRobotRobotBaseCartesianVelocityMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotRobotBaseDriver, CartesianVelocityActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, SoftRobotRobotBaseDriver device,
			CartesianVelocityActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		NetFragment fragment = new NetFragment("Robot base");
		NetFragment deviceFragment = new NetFragment("Device");
		fragment.add(deviceFragment);

		BaseVelocity rmpVel = deviceFragment.add(new BaseVelocity(device.getDeviceName()));
		rmpVel.getInVel().setDebug(10);

		ErrorNumberSwitchFragment error = deviceFragment.add(new ErrorNumberSwitchFragment(
				deviceFragment.addOutPort(new IntDataflow(), false, rmpVel.getOutError())));
		DataflowOutPort unknownError = error.getDefaultPort();

		DataflowOutPort concurrentAccessError = deviceFragment.addOutPort(new StateDataflow(), false,
				rmpVel.getOutErrorConcurrentAccess());
		DataflowOutPort drivesNotEnabledError = deviceFragment.addOutPort(new StateDataflow(), false,
				rmpVel.getOutErrorBaseFailed());

		Frame odometryFrame = device.getOdometryFrame();
		Frame odometryOrigin = device.getOdometryOrigin();
		DataflowInPort velPort = fragment.addInPort(new VelocityDataflow(odometryFrame, odometryOrigin,
				odometryFrame.getPoint(), odometryFrame.getOrientation()), false, rmpVel.getInVel());

		fragment.connect(actionResult.getOutPort(), velPort);

		fragment.addLinkBuilder(new WorldLinkBuilder(runtime));

		return new SoftRobotRobotBaseMapperResult(device, fragment, fragment.add(new TrueFragment()).getTrueOut(),
				concurrentAccessError, drivesNotEnabledError, unknownError);
	}

}
