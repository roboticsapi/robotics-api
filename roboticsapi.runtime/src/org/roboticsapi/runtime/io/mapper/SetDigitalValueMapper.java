/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.io.action.SetDigitalValue;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.mapper.BooleanHoldFragment;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.GoalActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class SetDigitalValueMapper implements ActionMapper<SoftRobotRuntime, SetDigitalValue> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, SetDigitalValue action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		final NetFragment fragment = new NetFragment("Set Digital Value");
		final SensorMappingContext context = new SensorMappingContext();

		final SensorMapperResult<Boolean> sensor = runtime.getMapperRegistry().mapSensor(runtime, action.getSensor(),
				fragment, context);
		final BooleanHoldFragment hold = fragment.add(new BooleanHoldFragment(sensor.getSensorPort()));
		final DataflowOutPort outPort = fragment.addOutPort(hold.getOutValue());
		final ActionResult result = new DigitalValueActionResult(outPort);

		return new GoalActionMapperResult(action, fragment, result);
	}

}
