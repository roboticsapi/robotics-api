/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.mapper;

import java.util.Map;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.framework.robot.action.SwitchToolAction;
import org.roboticsapi.framework.robot.parameter.RobotTool;

public class SwitchToolMapper implements ActionMapper<SwitchToolAction> {

	@Override
	public ActionResult map(SwitchToolAction action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> plans)
			throws MappingException, RpiException {

		RobotTool tool = action.getRobotTool();

		ToolActionResult ret = new ToolActionResult(action, RealtimeBoolean.TRUE,
				tool.getCenterOfMass().asRealtimeValue(),
				RealtimeVector.createFromConstant(new Vector(tool.getJx(), tool.getJy(), tool.getJz())),
				RealtimeDouble.createFromConstant(tool.getMass()));

		return ret;
	}
}
