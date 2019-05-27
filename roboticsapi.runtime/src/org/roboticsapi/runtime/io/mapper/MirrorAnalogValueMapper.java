/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.io.action.MirrorAnalogValue;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class MirrorAnalogValueMapper implements ActionMapper<SoftRobotRuntime, MirrorAnalogValue> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, MirrorAnalogValue action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		final NetFragment fragment = new NetFragment("Mirror Analog Value");
		final SensorMappingContext context = new SensorMappingContext();

		final SensorMapperResult<Double> sensor = runtime.getMapperRegistry().mapSensor(runtime, action.getSensor(),
				fragment, context);
		final DataflowOutPort outPort = fragment.addOutPort(sensor.getSensorPort());
		final ActionResult result = new AnalogValueActionResult(outPort);

		return new BaseActionMapperResult(action, fragment, result, ports.cancelPort);
	}
}
