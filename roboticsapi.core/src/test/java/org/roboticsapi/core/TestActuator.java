/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.actuator.OverrideParameter.Scaling;

public class TestActuator extends AbstractActuator<TestActuatorDriver> {

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
		addMaximumParameters(new OverrideParameter(1, Scaling.RELATIVE));
	}

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		addDefaultParameters(new OverrideParameter(0.1, Scaling.RELATIVE));
	}

}
