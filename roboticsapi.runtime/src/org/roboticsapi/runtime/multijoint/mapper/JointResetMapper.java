/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.multijoint.action.JointReset;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.GoalActionMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.rpi.RPIException;

public class JointResetMapper implements ActionMapper<SoftRobotRuntime, JointReset> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, JointReset action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		final NetFragment fragment = new NetFragment("ResetJoint");

		DataflowOutPort newJointPosition = fragment.addOutPort(new JointDataflow(null), false,
				fragment.add(new DoubleValue(action.getNewPosition())).getOutValue());

		GoalActionMapperResult goalActionMapperResult = new GoalActionMapperResult(action, fragment,
				new JointResetActionResult(newJointPosition));

		return goalActionMapperResult;
	}
}
