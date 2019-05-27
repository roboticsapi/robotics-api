/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.multijoint.action.AllJointVelocitiesScaledMotion;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.mapper.DoubleMinFragment;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.multijoint.JointVelDataflow;
import org.roboticsapi.runtime.rpi.RPIException;

public class AllJointVelocitiesScaledMotionMapper
		extends JointVelocityGuardedMotionMapper<AllJointVelocitiesScaledMotion> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, AllJointVelocitiesScaledMotion action,
			DeviceParameterBag parameters, ActionMappingContext ports) throws MappingException, RPIException {
		return super.map(runtime, action, parameters, ports);
	}

	@Override
	protected List<DataflowOutPort> createJointVelocityGuarding(AllJointVelocitiesScaledMotion action,
			NetFragment fragment, List<DataflowOutPort> jointCmdVel, List<DataflowOutPort> jointScalingFactors)
			throws MappingException {

		DoubleMinFragment scalingMinimum = fragment.add(
				new DoubleMinFragment(jointScalingFactors.toArray(new DataflowOutPort[jointScalingFactors.size()])));

		List<DataflowOutPort> jointGuardedVel = new ArrayList<DataflowOutPort>();

		for (int i = 0; i < jointCmdVel.size(); i++) {
			DataflowOutPort cmdVel = jointCmdVel.get(i);

			DoubleMultiply mult = fragment.add(new DoubleMultiply());

			fragment.connect(fragment.reinterpret(cmdVel, new DoubleDataflow()),
					fragment.addInPort(new DoubleDataflow(), true, mult.getInFirst()));

			fragment.connect(scalingMinimum.getOutMin(),
					fragment.addInPort(new DoubleDataflow(), true, mult.getInSecond()));

			jointGuardedVel
					.add(fragment.reinterpret(fragment.addOutPort(new DoubleDataflow(), true, mult.getOutValue()),
							new JointVelDataflow(action.getMultiJointDevice().getDriver().getJoint(i).getDriver())));
		}

		return jointGuardedVel;

	}

	@Override
	protected ActionResult createActionResult(DataflowOutPort outPort) {
		return new JointPositionActionResult(outPort);
	}

}
