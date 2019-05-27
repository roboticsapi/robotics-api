/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;

class SoftRobotRuntimeCommand extends RuntimeCommand implements MappedCommand {
	private CommandMapperResult result = null;
	private final SoftRobotCommandPorts ports = new SoftRobotCommandPorts();

	public SoftRobotRuntimeCommand(Action action, Actuator device, DeviceParameterBag parameters)
			throws RoboticsException {
		super(action, device, parameters);
	}

	@Override
	public SoftRobotRuntime getRuntime() {
		return (SoftRobotRuntime) super.getRuntime();
	}

	@Override
	public void seal() throws RoboticsException {
		if (isSealed()) {
			return;
		}

		super.seal();
		try {
			result = getRuntime().getMapperRegistry().mapCommand(getRuntime(), this, ports.outStart, ports.outStop,
					ports.outCancel, ports.outOverride);
		} catch (MappingException e) {
			throw new RoboticsException(e);
		}
	}

	@Override
	public CommandMapperResult getMappingResult() {
		return result;
	}

	@Override
	public SoftRobotCommandPorts getMappingPorts() {
		return ports;
	}
}