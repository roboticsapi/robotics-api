/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rcc;

import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.mapping.BasicCommandHandle;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntimeCommand;

class RccRuntimeCommand extends RpiRuntimeCommand {

	public RccRuntimeCommand(Action action, ActuatorDriver device, RccRuntime runtime, DeviceParameterBag parameters)
			throws RoboticsException {
		super(runtime, action, device, parameters);
	}

	@Override
	protected BasicCommandHandle createCommandHandle(NetHandle handle, Map<CommandResult, NetcommValue> resultMap)
			throws CommandException {
		return new BasicCommandHandle(this, handle, resultMap);
	}
}