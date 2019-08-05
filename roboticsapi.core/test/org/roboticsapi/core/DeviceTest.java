/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.SingleDeviceInterfaceFactory;
import org.roboticsapi.mockclass.TestActuator;
import org.roboticsapi.mockclass.TestDeviceInterface1;
import org.roboticsapi.mockclass.TestDeviceInterface1Impl;
import org.roboticsapi.mockclass.TestDeviceInterface1ImplSub;
import org.roboticsapi.mockclass.TestDeviceInterface2;
import org.roboticsapi.mockclass.TestDeviceInterface2Impl;

public class DeviceTest {

	@Test
	public void testUseReturnsInterfaceAfterAdd() {
		TestActuator dev = new TestActuator(null);

		dev.addInterfaceFactory(new SingleDeviceInterfaceFactory<TestDeviceInterface1Impl>() {

			@Override
			protected TestDeviceInterface1Impl build() {
				return new TestDeviceInterface1Impl();
			}

		});

		Assert.assertNotNull(dev.use(TestDeviceInterface1.class));
		Assert.assertTrue(dev.use(TestDeviceInterface1.class) instanceof TestDeviceInterface1Impl);
	}

	@Test
	public void testAddingSubclassInterfaceReplacesSuperclassInterface() {
		TestActuator dev = new TestActuator(null);

		dev.addInterfaceFactory(new SingleDeviceInterfaceFactory<TestDeviceInterface1Impl>() {

			@Override
			protected TestDeviceInterface1Impl build() {
				return new TestDeviceInterface1Impl();
			}
		});

		dev.addInterfaceFactory(new SingleDeviceInterfaceFactory<TestDeviceInterface1ImplSub>() {

			@Override
			protected TestDeviceInterface1ImplSub build() {
				return new TestDeviceInterface1ImplSub();
			}
		});

		// Assert.assertEquals(1, dev.getInterfaces().size());

		Assert.assertTrue(dev.use(TestDeviceInterface1.class) instanceof TestDeviceInterface1ImplSub);
	}

	@Test
	public void testGetInterfacesReturnsTypesOfAllAddedInterfaces() {
		TestActuator dev = new TestActuator(null);
		dev.addInterfaceFactory(new SingleDeviceInterfaceFactory<TestDeviceInterface1Impl>() {

			@Override
			protected TestDeviceInterface1Impl build() {
				return new TestDeviceInterface1Impl();
			}
		});
		dev.addInterfaceFactory(new SingleDeviceInterfaceFactory<TestDeviceInterface2Impl>() {

			@Override
			protected TestDeviceInterface2Impl build() {
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
