/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.device;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.mockclass.MockDeviceDriverImpl;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.device.AbstractPhysicalDevice;

public class AbstractPhysicalDeviceTest {
	private class MockAbstractPhysicalDevice<DD extends DeviceDriver> extends AbstractPhysicalDevice<DD> {
		@Override
		protected void setupDriver(DD driver) {
			// Auto-generated method stub
		}
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
	public void testMethodSetBaseAndMethodGetBaseExpectingTheTestBaseFrame() {
		Frame testBaseFrame = new Frame();

		testAbstractPhysicalDevice.setBase(testBaseFrame);

		assertEquals(testBaseFrame, testAbstractPhysicalDevice.getBase());
	}

	@Test
	public void testOverrideProtectedMethodValidateConfigurationPropertiesByUsingInheritedMethodInitializeExpectingIsInitializedTrue()
			throws InitializationException {
		RoboticsContext testInitializationContext = new RoboticsContextImpl("mock");

		DeviceDriver testDeviceDriver = new MockDeviceDriverImpl(null);
		testInitializationContext.initialize(testDeviceDriver);

		assertFalse(testAbstractPhysicalDevice.isInitialized());

		testAbstractPhysicalDevice.setDriver(testDeviceDriver);

		testInitializationContext.initialize(testAbstractPhysicalDevice);

		assertTrue(testAbstractPhysicalDevice.isInitialized());
	}

	@Test(expected = ConfigurationException.class)
	public void testOverrideProtectedMethodClearAutomaticConfigurationPropertiesByUsingInheritedMethodInitializeWithUninitializedDeviceDriverExpectingConfigurationException()
			throws InitializationException {
		DeviceDriver testDeviceDriver = new MockDeviceDriverImpl(null);

		RoboticsContext testInitializationContext = new RoboticsContextImpl("mock");

		assertFalse(testAbstractPhysicalDevice.isInitialized());

		testAbstractPhysicalDevice.setDriver(testDeviceDriver);

		testInitializationContext.initialize(testAbstractPhysicalDevice);
	}
}
