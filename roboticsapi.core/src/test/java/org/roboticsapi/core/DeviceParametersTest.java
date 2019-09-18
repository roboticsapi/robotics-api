/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.actuator.OverrideParameter.Scaling;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;

public class DeviceParametersTest {

	private TestActuator actuator;
	private TestRuntime runtime;
	private TestActuatorDriver driver;

	@Before
	public void setup() throws InitializationException {
		RoboticsContext context = new RoboticsContextImpl("Default context");
		runtime = new TestRuntime();
		context.initialize(runtime);
		actuator = new TestActuator();
		context.initialize(actuator);
		driver = new TestActuatorDriver(actuator, runtime);
		context.initialize(driver);
	}

	@Test
	public void testActuatorUsesMaxmimumParametersAsDefaultParameters() {

		OverrideParameter params = actuator.getDefaultParameters().get(OverrideParameter.class);

		Assert.assertNotNull(params);

		Assert.assertEquals(0.1d, params.getOverride().getCheapValue(), 0.001);
	}

	@Test
	public void testCorrectDefaultParametersReplaceMaximumParameters() throws InvalidParametersException {
		actuator.addDefaultParameters(new OverrideParameter(1d, Scaling.ABSOLUTE));

		OverrideParameter params = actuator.getDefaultParameters().get(OverrideParameter.class);

		Assert.assertNotNull(params);

		Assert.assertEquals(1d, params.getOverride().getCheapValue(), 0.001);
	}

	@Test
	public void testAddingIncorrectParametersThrowsException() {
		try {
			actuator.addDefaultParameters(new OverrideParameter(100d, Scaling.RELATIVE));

			Assert.fail("Expected exception");
		} catch (InvalidParametersException e) {
			OverrideParameter params = actuator.getDefaultParameters().get(OverrideParameter.class);

			Assert.assertNotNull(params);

			Assert.assertEquals(0.1d, params.getOverride().getCheapValue(), 0.001);
		}
	}

	@Test
	public void testCreatingRuntimeCommandThrowsExceptionWhenIllegalParameters() {
		OverrideParameter params = new OverrideParameter(100d, Scaling.RELATIVE);

		try {
			runtime.createRuntimeCommand(driver, new TestAction(), new DeviceParameterBag().withParameters(params));

			Assert.fail("Expected exception");
		} catch (InvalidParametersException e) {
			// expected
		} catch (RoboticsException e) {
			Assert.fail(e.getMessage());
		}
	}
}
