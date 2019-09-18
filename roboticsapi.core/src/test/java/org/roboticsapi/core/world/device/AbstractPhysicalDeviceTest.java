/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.device;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;

public class AbstractPhysicalDeviceTest {
	private class MockAbstractPhysicalDevice<DD extends DeviceDriver> extends AbstractPhysicalDevice<DD> {
	}

	private AbstractPhysicalDevice<DeviceDriver> testAbstractPhysicalDevice = null;

	@Before
	public void setup() {
		testAbstractPhysicalDevice = new MockAbstractPhysicalDevice<DeviceDriver>();
	}

	@After
	public void teardown() {
		testAbstractPhysicalDevice = null;
	}

	@Test
	public void testOverrideProtectedMethodValidateConfigurationPropertiesByUsingInheritedMethodInitializeExpectingIsInitializedTrue()
			throws InitializationException {
		RoboticsContext testInitializationContext = new RoboticsContextImpl("mock");

		assertFalse(testAbstractPhysicalDevice.isInitialized());
		testInitializationContext.initialize(testAbstractPhysicalDevice);
		assertTrue(testAbstractPhysicalDevice.isInitialized());
	}

}
