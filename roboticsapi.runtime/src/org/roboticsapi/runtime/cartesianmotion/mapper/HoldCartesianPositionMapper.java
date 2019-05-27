/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.cartesianmotion.action.HoldCartesianPosition;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;

public class HoldCartesianPositionMapper implements ActionMapper<SoftRobotRuntime, HoldCartesianPosition> {

	public final class BaseActionMapperResultExtension extends BaseActionMapperResult {

		private BaseActionMapperResultExtension(Action action, NetFragment fragment, ActionResult result,
				ActionMappingContext ports) {
			super(action, fragment, result, ports.cancelPort); // completed when
																// cancelled
		}

	}

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, HoldCartesianPosition action, DeviceParameterBag parameters,
			final ActionMappingContext ports) throws MappingException, RPIException {

		final MotionCenterParameter mp = parameters.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new MappingException("No motion center parameters given");
		}

		// create net fragment
		final NetFragment ret = new NetFragment("PositionHold");

		// the transformation between goal and motion center should be identity
		FrameFromPosRot fc = ret.add(new FrameFromPosRot(0d, 0d, 0d, 0d, 0d, 0d));

		ActionResult result = new CartesianPositionActionResult(ret
				.addOutPort(new RelationDataflow(action.getPosition(), mp.getMotionCenter()), true, fc.getOutValue()));
		return new BaseActionMapperResultExtension(action, ret, result, ports);
	}

}
