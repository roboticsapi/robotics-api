/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.exception.RoboticsException;

public class TestDeviceInterface2Impl implements TestDeviceInterface2 {

	@Override
	public void cancel() throws RoboticsException {
	}

	@Override
	public void endExecute() throws RoboticsException {
	}

	@Override
	public void addDefaultParameters(DeviceParameters newParameters) throws InvalidParametersException {
	}

	@Override
	public DeviceParameterBag getDefaultParameters() {
		return null;
	}

	@Override
	public Device getDevice() {
		return null;
	}

}
