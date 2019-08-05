/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.mockclass.TestAction;
import org.roboticsapi.mockclass.TestActuator;
import org.roboticsapi.mockclass.TestActuatorDriver;
import org.roboticsapi.mockclass.TestRuntime;

public class DeviceParametersTest {

	private TestActuator actuator;
	private TestRuntime runtime;
	private TestActuatorDriver driver;

	@Before
	public void setup() throws InitializationException {
		RoboticsContext context = new RoboticsContextImpl("Default context");
		runtime = new TestRuntime();
		context.initialize(runtime);
		driver = new TestActuatorDriver(runtime);
		context.initialize(driver);
		actuator = new TestActuator("Test", runtime);
		actuator.setDriver(driver);
		context.initialize(actuator);
	}

	@Test
	public void testActuatorUsesMaxmimumParametersAsDefaultParameters() {

		CartesianParameters params = actuator.getDefaultParameters().get(CartesianParameters.class);

		Assert.assertNotNull(params);

		Assert.assertEquals(10d, params.getMaximumPositionVelocity(), 0.001);
		Assert.assertEquals(20d, params.getMaximumPositionAcceleration(), 0.001);
		Assert.assertEquals(4 * Math.PI, params.getMaximumRotationVelocity(), 0.001);
		Assert.assertEquals(8 * Math.PI, params.getMaximumRotationAcceleration(), 0.001);
	}

	@Test
	public void testCorrectDefaultParametersReplaceMaximumParameters() throws InvalidParametersException {
		actuator.addDefaultParameters(new CartesianParameters(1d, 2d, 1d, 2d));

		CartesianParameters params = actuator.getDefaultParameters().get(CartesianParameters.class);

		Assert.assertNotNull(params);

		Assert.assertEquals(1d, params.getMaximumPositionVelocity(), 0.001);
		Assert.assertEquals(2d, params.getMaximumPositionAcceleration(), 0.001);
		Assert.assertEquals(1d, params.getMaximumRotationVelocity(), 0.001);
		Assert.assertEquals(2d, params.getMaximumRotationAcceleration(), 0.001);
	}

	@Test
	public void testAddingIncorrectParametersThrowsException() {
		try {
			actuator.addDefaultParameters(new CartesianParameters(100d, 200d, 100d, 200d));

			Assert.fail("Expected exception");
		} catch (InvalidParametersException e) {
			CartesianParameters params = actuator.getDefaultParameters().get(CartesianParameters.class);

			Assert.assertNotNull(params);

			Assert.assertEquals(10d, params.getMaximumPositionVelocity(), 0.001);
			Assert.assertEquals(20d, params.getMaximumPositionAcceleration(), 0.001);
			Assert.assertEquals(4 * Math.PI, params.getMaximumRotationVelocity(), 0.001);
			Assert.assertEquals(8 * Math.PI, params.getMaximumRotationAcceleration(), 0.001);
		}
	}

	@Test
	public void testCreatingRuntimeCommandThrowsExceptionWhenIllegalParameters() {
		CartesianParameters params = new CartesianParameters(100d, 200d, 100d, 200d);

		try {
			runtime.createRuntimeCommand(actuator, new TestAction(), new DeviceParameterBag().withParameters(params));

			Assert.fail("Expected exception");
		} catch (RoboticsException e) {

		}
	}
}
