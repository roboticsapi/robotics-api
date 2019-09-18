/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.Device.InterfaceListener;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.mockclass.MockDeviceDriverImpl;

public class AbstractDeviceTest {
	private class MockDevice<DD extends DeviceDriver> extends AbstractDevice<DD> {
	}

	private DeviceDriver mockDeviceDriver = null;
	private AbstractDevice<DeviceDriver> mockDevice = null;

	@Before
	public void setup() {
		mockDevice = new MockDevice<DeviceDriver>();
		mockDeviceDriver = new MockDeviceDriverImpl(mockDevice, null);
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
		DeviceInterfaceFactory<DeviceInterface> testFactory = new DeviceInterfaceFactory<DeviceInterface>() {
			@Override
			public DeviceInterface build() {
				// TODO Auto-generated method stub
				return null;
			}
		};

		InterfaceListener testListener = new InterfaceListener() {
			@Override
			public void interfaceAdded(Device device, Class<? extends DeviceInterface> type) {
				// TODO Auto-generated method stub

			}

			@Override
			public void interfaceRemoved(Device device, Class<? extends DeviceInterface> type) {
				// TODO Auto-generated method stub

			}
		};

		mockDevice.addInterfaceFactory(DeviceInterface.class, testFactory);

		mockDevice.addInterfaceListener(testListener);
	}
}
