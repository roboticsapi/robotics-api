/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.Map;

import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.actuator.AbstractActuator;

public class TestActuator extends AbstractActuator<TestActuatorDriver> {

	public TestActuator(String name, RoboticsRuntime runtime) {
		this(runtime);
		setName(name);
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		super.fillAutomaticConfigurationProperties(createdObjects);

		createdObjects.put("driver", getDriver());

	}

	public TestActuator(RoboticsRuntime runtime) {
		setDriver(new TestActuatorDriver(runtime));
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {

	}

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
		addMaximumParameters(new CartesianParameters(10, 20, 4 * Math.PI, 8 * Math.PI));

	}

	@Override
	protected void setupDriver(TestActuatorDriver driver) {
	}

}
