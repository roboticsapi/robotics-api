/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;

public class MockRuntimeCommand extends RuntimeCommand {
	public MockRuntimeCommand(Action action, Actuator device, DeviceParameterBag parameters) throws RoboticsException {
		super(action, device, parameters);
	}
}
