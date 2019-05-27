/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.action;

import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.platform.action.DriveTo;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class DriveToMapper implements ActionMapper<SoftRobotRuntime, DriveTo> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, DriveTo action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		if (parameters.get(MotionCenterParameter.class) == null) {
			throw new MappingException("No motion center given.");
		}

		NetFragment fragment = new NetFragment("DriveTo");
		NetFragment blocks = new NetFragment("Cartesian Goal");
		FrameFromPosRot frame = blocks.add(new FrameFromPosRot(0d, 0d, 0d, 0d, 0d, 0d));
		DataflowOutPort goalPort = blocks.addOutPort(
				new RelationDataflow(action.getGoal(), parameters.get(MotionCenterParameter.class).getMotionCenter()),
				false, frame.getOutValue());
		fragment.add(blocks);
		CartesianGoalActionResult result = new CartesianGoalActionResult(goalPort);
		return new DriveToMapperResult(action, fragment, result);
	}

}
