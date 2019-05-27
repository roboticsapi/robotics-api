/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.robot.action.JointSpaceConverter;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.WrappedActionMapperResult;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.rpi.RPIException;

@SuppressWarnings("rawtypes")
public class JointSpaceConverterMapper implements ActionMapper<SoftRobotRuntime, JointSpaceConverter> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, JointSpaceConverter action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		NetFragment fragment = new NetFragment("JointSpaceConverter");

		ActionMapperResult mappedInnerAction = runtime.getMapperRegistry().mapAction(runtime, action.getInnerAction(),
				parameters, ports.cancelPort, ports.overridePort, ports.actionPlans);

		fragment.add(mappedInnerAction.getNetFragment());

		JointSpaceConverter<?> conv = action;
		DataflowOutPort outPort = fragment.addConverterLink(mappedInnerAction.getActionResult().getOutPort(),
				new JointsDataflow(conv.getDevice().getJointCount(), conv.getDevice().getDriver()));

		return new WrappedActionMapperResult(action, action.getInnerAction(), fragment,
				new JointPositionActionResult(outPort), mappedInnerAction);
	}

}
