/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;

public class MockDeviceImpl extends MockComposedEntityImpl implements Device {
	private DeviceDriver driver = null;
	private OperationState state = null;
	private boolean present = true;

	private final List<DeviceInterfaceFactory> deviceInterfaceFactories = new ArrayList<DeviceInterfaceFactory>();
	private final List<InterfaceFactoryListener> interfaceFactoryListeners = new ArrayList<InterfaceFactoryListener>();
	private final List<OperationStateListener> operationStateListeners = new ArrayList<OperationStateListener>();

	@Override
	public void addOperationStateListener(OperationStateListener listener) {
		operationStateListeners.add(listener);
	}

	@Override
	public void removeOperationStateListener(OperationStateListener listener) {
		if (!operationStateListeners.isEmpty()) {
			operationStateListeners.remove(listener);
		}
	}

	public void setState(OperationState state) {
		this.state = state;
	}

	@Override
	public OperationState getState() {
		return state;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	@Override
	public boolean isPresent() {
		return present;
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
		deviceInterfaceFactories.add(factory);
	}

	@Override
	public void addInterfaceFactoryListener(InterfaceFactoryListener listener) {
		interfaceFactoryListeners.add(listener);
	}

	@Override
	public void removeInterfaceFactoryListener(InterfaceFactoryListener listener) {
		if (!interfaceFactoryListeners.isEmpty()) {
			interfaceFactoryListeners.remove(listener);
		}
	}

	public void setDriver(DeviceDriver driver) {
		this.driver = driver;
	}

	@Override
	public DeviceDriver getDriver() {
		return driver;
	}
}
