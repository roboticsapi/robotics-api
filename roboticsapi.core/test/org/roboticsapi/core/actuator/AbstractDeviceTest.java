/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.Device.InterfaceFactoryListener;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.actuator.AbstractDevice;
import org.roboticsapi.mockclass.MockDeviceDriverImpl;

public class AbstractDeviceTest {
	private class MockDevice<DD extends DeviceDriver> extends AbstractDevice<DD> {
		@Override
		protected void setupDriver(DD driver) {
			// empty implementation
		}
	}

	private DeviceDriver mockDeviceDriver = null;
	private AbstractDevice<DeviceDriver> mockDevice = null;

	@Before
	public void setup() {
		mockDeviceDriver = new MockDeviceDriverImpl(null);
		mockDevice = new MockDevice<DeviceDriver>();
		mockDevice.setDriver(mockDeviceDriver);
	}

	@After
	public void teardown() {
		mockDeviceDriver = null;
		mockDevice = null;
	}

	@Test
	public void testUseWithNoDeviceInterfaceFactoryExpectingNullSimpleTest() {
		assertNull(mockDevice.use(DeviceInterface.class));
	}

	@Test
	public void testAddInterfaceFactoryListenerSimpleTest() {
		DeviceInterfaceFactory testFactory = new DeviceInterfaceFactory() {
			@Override
			public boolean canBuild(Class<? extends DeviceInterface> clazz) {
				return false;
			}

			@Override
			public <T extends DeviceInterface> T build(Class<T> clazz) {
				return null;
			}

			@Override
			public List<Class<? extends DeviceInterface>> getProvidedInterfaces() {
				return null;
			}
		};

		InterfaceFactoryListener testListener = new InterfaceFactoryListener() {
			@Override
			public void interfaceFactoryAdded(Device device, DeviceInterfaceFactory factory) {
				// empty implementation
			}
		};

		mockDevice.addInterfaceFactory(testFactory);

		mockDevice.addInterfaceFactoryListener(testListener);
	}
}
