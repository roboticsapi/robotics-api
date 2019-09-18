/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;

public class TestRuntimeCommand extends RuntimeCommand {

	public TestRuntimeCommand(Action action, ActuatorDriver device) throws RoboticsException {
		this(action, device, new DeviceParameterBag());
	}

	public TestRuntimeCommand(Action action, ActuatorDriver device, DeviceParameterBag parameters)
			throws RoboticsException {
		super(action, device, parameters);
	}

	@Override
	protected CommandHandle createHandle() throws RoboticsException {
		// TODO Auto-generated method stub
		return null;
	}

}
