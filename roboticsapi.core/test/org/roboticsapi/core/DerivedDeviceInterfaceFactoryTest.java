/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.roboticsapi.core.DerivedDeviceInterfaceFactory;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.EntityListener;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.entity.PropertyListener;
import org.roboticsapi.core.exception.EntityException;

public class DerivedDeviceInterfaceFactoryTest {
	private class MockDerivedDeviceInterfaceFactory<T extends DeviceInterface>
			extends DerivedDeviceInterfaceFactory<T> {
		@Override
		protected T build() {
			return null;
		}
	}

	private final DerivedDeviceInterfaceFactory<DeviceInterface> mockDDIF = new MockDerivedDeviceInterfaceFactory<DeviceInterface>();

	@Test
	public void testInterfaceFactoryAddedWithMockDeviceCallingTwiceToReachReturnStatement() {
		class MockDevice implements Device {
			@Override
			public void addOperationStateListener(OperationStateListener listener) {
			}

			@Override
			public void removeOperationStateListener(OperationStateListener listener) {
			}

			@Override
			public OperationState getState() {
				return null;
			}

			@Override
			public boolean isPresent() {
				return false;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void setName(String name) {
			}

			@Override
			public boolean isInitialized() {
				return false;
			}

			@Override
			public void addInitializationListener(InitializationListener listener) {
			}

			@Override
			public void removeInitializationListener(InitializationListener listener) {
			}

			@Override
			public Set<Entity> getChildren() {
				return null;
			}

			@Override
			public void addChild(Entity child) throws EntityException {
			}

			@Override
			public boolean canAddChild(Entity child) {
				return false;
			}

			@Override
			public void removeChild(Entity child) throws EntityException {
			}

			@Override
			public boolean canRemoveChild(Entity child) {
				return false;
			}

			@Override
			public ComposedEntity getParent() {
				return null;
			}

			@Override
			public void setParent(ComposedEntity parent) throws EntityException {
			}

			@Override
			public void addEntityListener(EntityListener l) {
			}

			@Override
			public void removeEntityListener(EntityListener l) {
			}

			@Override
			public void addProperty(Property property) {
			}

			@Override
			public Set<Property> getProperties() {
				return null;
			}

			@Override
			public <T extends Property> Set<T> getProperties(Class<T> type) {
				return null;
			}

			@Override
			public void addPropertyListener(PropertyListener l) {
			}

			@Override
			public void removePropertyListener(PropertyListener l) {
			}

			@Override
			public List<Class<? extends DeviceInterface>> getInterfaces() {
				return null;
			}

			@Override
			public <U extends DeviceInterface> U use(Class<U> deviceInterface) {
				return null;
			}

			@Override
			public void addInterfaceFactory(DeviceInterfaceFactory factory) {
			}

			@Override
			public void addInterfaceFactoryListener(InterfaceFactoryListener listener) {
			}

			@Override
			public void removeInterfaceFactoryListener(InterfaceFactoryListener listener) {
			}

			@Override
			public DeviceDriver getDriver() {
				return null;
			}

			@Override
			public <T extends Property> boolean hasProperty(Class<T> type) {
				return false;
			}

			@Override
			public void removeProperty(Property property) {

			}

			@Override
			public <T extends Property> void removeProperties(Class<T> type) {

			}
		}

		mockDDIF.interfaceFactoryAdded(new MockDevice(), null);
		mockDDIF.interfaceFactoryAdded(null, null);
	}

	@Test
	public void testUseWithEmptyFactoriesMapExpectingNull() {
		assertNull(mockDDIF.use(DeviceInterface.class));
	}
}
