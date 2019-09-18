/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;

public class MockRuntimeCommand extends RuntimeCommand {

	protected MockRuntimeCommand(Action action, ActuatorDriver device, DeviceParameterBag parameters)
			throws RoboticsException {
		super(action, device, parameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CommandHandle createHandle() throws RoboticsException {
		// TODO Auto-generated method stub
		return null;
	}

}
