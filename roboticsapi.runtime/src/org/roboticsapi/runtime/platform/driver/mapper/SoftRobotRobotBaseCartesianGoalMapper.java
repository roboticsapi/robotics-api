/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.world.WorldLinkBuilder;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistConditional;
import org.roboticsapi.runtime.world.primitives.TwistIsNull;
import org.roboticsapi.runtime.world.primitives.TwistPre;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.ErrorNumberSwitchFragment;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.platform.driver.SoftRobotRobotBaseDriver;
import org.roboticsapi.runtime.platform.primitives.BaseMonitor;
import org.roboticsapi.runtime.platform.primitives.BaseVelocity;
import org.roboticsapi.runtime.platform.primitives.OmnidirectionalBaseController;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.world.Frame;

public class SoftRobotRobotBaseCartesianGoalMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotRobotBaseDriver, CartesianGoalActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, SoftRobotRobotBaseDriver device,
			CartesianGoalActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		NetFragment fragment = new NetFragment("Robot base");
		NetFragment deviceFragment = new NetFragment("Device");
		NetFragment controllerFragment = new NetFragment("Controller");
		fragment.add(deviceFragment);
		fragment.add(controllerFragment);

		OmnidirectionalBaseController baseCtrl = controllerFragment
				.add(new OmnidirectionalBaseController(0.3, 0.3, 0.3, 3.0, 3.0, 3.0));
		Frame odometryFrame = device.getOdometryFrame();
		Frame odometryOrigin = device.getOdometryOrigin();
		DataflowInPort inGoal = controllerFragment.addInPort(new RelationDataflow(odometryOrigin, odometryFrame), true,
				baseCtrl.getInDest());
		DataflowOutPort completed = controllerFragment.addOutPort(new StateDataflow(), false,
				baseCtrl.getOutCompleted());

		BaseMonitor baseMon = controllerFragment.add(new BaseMonitor(device.getDeviceName()));
		baseCtrl.getInPos().connectTo(baseMon.getOutPos());

		TwistPre lastTwist = controllerFragment.add(new TwistPre());
		controllerFragment.connect(baseCtrl.getOutVel(), lastTwist.getInValue());

		TwistIsNull isFirst = controllerFragment.add(new TwistIsNull());
		controllerFragment.connect(lastTwist.getOutValue(), isFirst.getInValue());

		TwistConditional twist = controllerFragment.add(new TwistConditional());
		DataflowInPort falseIn = controllerFragment.addInPort(new VelocityDataflow(odometryFrame, odometryOrigin,
				odometryFrame.getPoint(), odometryFrame.getOrientation()), true, twist.getInFalse());
		DataflowOutPort lastOut = controllerFragment.addOutPort(new VelocityDataflow(odometryFrame, odometryOrigin,
				odometryFrame.getPoint(), odometryFrame.getOrientation()), true, lastTwist.getOutValue());
		controllerFragment.connect(lastOut, falseIn);

		// twist.getInFalse().connectTo(lastTwist.getOutValue());
		twist.getInTrue().connectTo(baseMon.getOutVel());
		twist.getInCondition().connectTo(isFirst.getOutValue());
		baseCtrl.getInVel().connectTo(twist.getOutValue());

		BaseVelocity rmpVel = deviceFragment.add(new BaseVelocity(device.getDeviceName()));
		rmpVel.getInVel().connectTo(baseCtrl.getOutVel());

		ErrorNumberSwitchFragment error = deviceFragment.add(new ErrorNumberSwitchFragment(
				deviceFragment.addOutPort(new IntDataflow(), false, rmpVel.getOutError())));
		// DataflowOutPort concurrentAccessError = error.getCasePort(1);
		// DataflowOutPort drivesNotEnabledError = error.getCasePort(2);
		DataflowOutPort unknownError = error.getDefaultPort();

		DataflowOutPort concurrentAccessError = deviceFragment.addOutPort(new StateDataflow(), false,
				rmpVel.getOutErrorConcurrentAccess());
		DataflowOutPort drivesNotEnabledError = deviceFragment.addOutPort(new StateDataflow(), false,
				rmpVel.getOutErrorBaseFailed());

		fragment.connect(actionResult.getOutPort(), inGoal);
		fragment.addLinkBuilder(new WorldLinkBuilder(runtime));

		return new SoftRobotRobotBaseMapperResult(device, fragment, completed, concurrentAccessError,
				drivesNotEnabledError, unknownError);
	}

}
