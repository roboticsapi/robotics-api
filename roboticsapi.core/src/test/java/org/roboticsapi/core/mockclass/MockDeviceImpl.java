/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import java.util.List;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.DeviceInterfaceModifier;
import org.roboticsapi.core.RoboticsRuntime;

public class MockDeviceImpl extends MockRoboticsEntityImpl implements Device {

	@Override
	public void addOperationStateListener(OperationStateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeOperationStateListener(OperationStateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public OperationState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Class<? extends DeviceInterface>> getInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends DeviceInterface> U use(Class<U> deviceInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends DeviceInterface> U use(Class<U> deviceInterface, DeviceInterface compatibleWith) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends DeviceInterface> U use(Class<U> deviceInterface, RoboticsRuntime compatibleWith) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DeviceInterface> void addInterfaceFactory(Class<T> type, DeviceInterfaceFactory<T> factory) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DeviceInterface> void removeInterfaceFactory(Class<T> type, DeviceInterfaceFactory<T> factory) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DeviceInterface> void addInterfaceModifier(Class<T> type, DeviceInterfaceModifier<T> factory) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DeviceInterface> void removeInterfaceModifier(Class<T> type,
			DeviceInterfaceModifier<T> modifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInterfaceListener(InterfaceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeInterfaceListener(InterfaceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public DeviceDriver getDriver(RoboticsRuntime runtime) {
		// TODO Auto-generated method stub
		return null;
	}
}
