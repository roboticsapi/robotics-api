/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DeviceTest {

	@Test
	public void testUseReturnsInterfaceAfterAdd() {
		TestActuator dev = new TestActuator();

		dev.addInterfaceFactory(TestDeviceInterface1.class, new DeviceInterfaceFactory<TestDeviceInterface1>() {
			@Override
			public TestDeviceInterface1 build() {
				return new TestDeviceInterface1Impl();
			}
		});

		Assert.assertNotNull(dev.use(TestDeviceInterface1.class));
		Assert.assertTrue(dev.use(TestDeviceInterface1.class) instanceof TestDeviceInterface1Impl);
	}

	@Test
	public void testGetInterfacesReturnsTypesOfAllAddedInterfaces() {
		TestActuator dev = new TestActuator();
		dev.addInterfaceFactory(TestDeviceInterface1Impl.class, new DeviceInterfaceFactory<TestDeviceInterface1Impl>() {

			@Override
			public TestDeviceInterface1Impl build() {
				return new TestDeviceInterface1Impl();
			}
		});
		dev.addInterfaceFactory(TestDeviceInterface2Impl.class, new DeviceInterfaceFactory<TestDeviceInterface2Impl>() {
			@Override
			public TestDeviceInterface2Impl build() {
				return new TestDeviceInterface2Impl();
			}
		});

		Assert.assertTrue(containsSubclass(dev.getInterfaces(), TestDeviceInterface1.class));
		Assert.assertTrue(containsSubclass(dev.getInterfaces(), TestDeviceInterface2.class));
	}

	private boolean containsSubclass(List<Class<? extends DeviceInterface>> list,
			Class<? extends DeviceInterface> superclass) {
		for (Class<?> c : list) {
			if (superclass.isAssignableFrom(c)) {
				return true;
			}
		}
		return false;
	}
}
